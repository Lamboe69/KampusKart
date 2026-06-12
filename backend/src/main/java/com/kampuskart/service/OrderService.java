package com.kampuskart.service;

import com.kampuskart.dto.OrderDto;
import com.kampuskart.entity.*;
import com.kampuskart.repository.*;
import com.kampuskart.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final TransactionRepository transactionRepo;
    private final NotificationRepository notificationRepo;

    public OrderService(OrderRepository orderRepo, ProductRepository productRepo,
                        UserRepository userRepo, TransactionRepository transactionRepo,
                        NotificationRepository notificationRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.transactionRepo = transactionRepo;
        this.notificationRepo = notificationRepo;
    }

    public List<OrderDto> list(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepo.findById(principal.getId()).orElseThrow();
        if ("admin".equals(user.getRole())) {
            return orderRepo.findAll().stream().map(OrderDto::from).collect(Collectors.toList());
        }
        Set<Order> orders = new LinkedHashSet<>();
        orders.addAll(orderRepo.findByBuyerIdOrderByCreatedAtDesc(principal.getId()));
        orders.addAll(orderRepo.findBySellerIdOrderByCreatedAtDesc(principal.getId()));
        return orders.stream().map(OrderDto::from).collect(Collectors.toList());
    }

    public OrderDto getById(Authentication auth, Long id) {
        Order o = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return OrderDto.from(o);
    }

    @Transactional
    public OrderDto create(Authentication auth, Map<String, Object> body) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = Integer.parseInt(body.get("quantity").toString());

        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        User buyer = userRepo.findById(principal.getId())
            .orElseThrow(() -> new RuntimeException("Buyer not found"));

        User seller = userRepo.findByEmail(product.getSellerName())
            .orElse(null);
        if (seller == null) {
            List<User> sellers = userRepo.findByName(product.getSellerName());
            seller = sellers.isEmpty() ? null : sellers.get(0);
        }

        BigDecimal deliveryFee = product.getDeliveryFee() != null ? product.getDeliveryFee() : BigDecimal.ZERO;
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(quantity)).add(deliveryFee);

        if (buyer.getBalance().compareTo(total) < 0) {
            throw new RuntimeException("Insufficient balance. Please top up your wallet.");
        }

        buyer.setBalance(buyer.getBalance().subtract(total));
        buyer.setPendingBalance(buyer.getPendingBalance().add(total));
        userRepo.save(buyer);

        if (seller != null) {
            seller.setPendingBalance(seller.getPendingBalance().add(total));
            userRepo.save(seller);
        }

        transactionRepo.save(new Transaction(buyer.getId(), total, "debit",
            "Payment held in escrow for " + product.getTitle() + " x" + quantity));

        Order order = new Order();
        order.setBuyerId(principal.getId());
        order.setSellerId(seller != null ? seller.getId() : 0);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotal(total);
        order.setDeliveryFee(deliveryFee);
        order.setStatus("pending");
        order.setDeliveryAddress((String) body.get("deliveryAddress"));
        order.setDeliveryCampus((String) body.get("deliveryCampus"));
        order.setPaymentMethod((String) body.getOrDefault("paymentMethod", "mobile_money"));
        order.setBuyerName(principal.getEmail());
        order.setSellerName(product.getSellerName());
        order.setProductTitle(product.getTitle());
        order.setProductImage(product.getImage());
        order = orderRepo.save(order);

        if (seller != null) {
            notificationRepo.save(new Notification(seller.getId(),
                "New Order", "You received a new order for " + product.getTitle(), "order"));
        }

        return OrderDto.from(order);
    }

    @Transactional
    public OrderDto updateStatus(Authentication auth, Long id, String status) {
        Order order = orderRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        if ("completed".equals(status)) {
            BigDecimal platformFee = order.getTotal().multiply(BigDecimal.valueOf(0.10));
            BigDecimal sellerAmount = order.getTotal().subtract(platformFee);

            User buyer = userRepo.findById(order.getBuyerId()).orElse(null);
            if (buyer != null) {
                buyer.setPendingBalance(buyer.getPendingBalance().subtract(order.getTotal()));
                userRepo.save(buyer);
            }

            User seller = userRepo.findById(order.getSellerId()).orElse(null);
            if (seller != null) {
                seller.setBalance(seller.getBalance().add(sellerAmount));
                seller.setPendingBalance(seller.getPendingBalance().subtract(order.getTotal()));
                seller.setTotalEarned(seller.getTotalEarned().add(sellerAmount));
                userRepo.save(seller);
                transactionRepo.save(new Transaction(seller.getId(), sellerAmount, "credit",
                    "Payment received for order #" + id));
                notificationRepo.save(new Notification(seller.getId(),
                    "Payment Released", "UGX " + sellerAmount + " has been released for order #" + id, "payment"));
            }

            if (buyer != null) {
                transactionRepo.save(new Transaction(buyer.getId(), order.getTotal(), "credit",
                    "Escrow released for order #" + id));
                notificationRepo.save(new Notification(buyer.getId(),
                    "Order Completed", "Your order #" + id + " is complete. Escrow released.", "order"));
            }

        } else if ("cancelled".equals(status)) {
            User buyer = userRepo.findById(order.getBuyerId()).orElse(null);
            if (buyer != null) {
                buyer.setPendingBalance(buyer.getPendingBalance().subtract(order.getTotal()));
                buyer.setBalance(buyer.getBalance().add(order.getTotal()));
                userRepo.save(buyer);
                transactionRepo.save(new Transaction(buyer.getId(), order.getTotal(), "credit",
                    "Refund for cancelled order #" + id));
                notificationRepo.save(new Notification(buyer.getId(),
                    "Order Cancelled", "UGX " + order.getTotal() + " refunded for order #" + id, "order"));
            }

            User seller = userRepo.findById(order.getSellerId()).orElse(null);
            if (seller != null) {
                seller.setPendingBalance(seller.getPendingBalance().subtract(order.getTotal()));
                userRepo.save(seller);
            }
        }

        orderRepo.save(order);
        return OrderDto.from(order);
    }
}
