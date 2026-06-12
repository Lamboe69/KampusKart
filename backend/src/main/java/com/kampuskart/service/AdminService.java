package com.kampuskart.service;

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
public class AdminService {
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final VerificationRepository verificationRepo;
    private final DisputeRepository disputeRepo;
    private final PayoutRepository payoutRepo;
    private final TransactionRepository transactionRepo;
    private final NotificationRepository notificationRepo;

    public AdminService(UserRepository userRepo, ProductRepository productRepo,
                        OrderRepository orderRepo, VerificationRepository verificationRepo,
                        DisputeRepository disputeRepo, PayoutRepository payoutRepo,
                        TransactionRepository transactionRepo,
                        NotificationRepository notificationRepo) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.verificationRepo = verificationRepo;
        this.disputeRepo = disputeRepo;
        this.payoutRepo = payoutRepo;
        this.transactionRepo = transactionRepo;
        this.notificationRepo = notificationRepo;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepo.count());
        stats.put("totalSellers", userRepo.countByRole("seller") + userRepo.countByRole("shop"));
        stats.put("totalProducts", productRepo.count());
        stats.put("totalOrders", orderRepo.count());
        stats.put("pendingOrders", orderRepo.countByStatus("pending"));
        stats.put("completedOrders", orderRepo.countByStatus("completed"));
        stats.put("totalRevenue", BigDecimal.ZERO);
        stats.put("platformFees", BigDecimal.ZERO);
        return stats;
    }

    public List<Verification> getVerifications() {
        return verificationRepo.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Verification updateVerification(Long id, String status, String adminNote) {
        Verification v = verificationRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Verification not found"));
        v.setStatus(status);
        v.setAdminNote(adminNote);
        v.setUpdatedAt(LocalDateTime.now());
        verificationRepo.save(v);

        User user = userRepo.findById(v.getUserId()).orElse(null);
        if (user != null) {
            user.setVerified("approved".equals(status));
            user.setUpdatedAt(LocalDateTime.now());
            userRepo.save(user);
            notificationRepo.save(new Notification(user.getId(),
                "Verification " + status, "Your verification request has been " + status, "info"));
        }
        return v;
    }

    public List<Dispute> getDisputes() {
        return disputeRepo.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Dispute resolveDispute(Long id, String status, String note, BigDecimal refundAmount, Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Dispute d = disputeRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Dispute not found"));
        d.setStatus(status);
        d.setResolutionNote(note);
        d.setResolvedBy(principal.getId());
        d.setRefundAmount(refundAmount);
        d.setUpdatedAt(LocalDateTime.now());
        disputeRepo.save(d);

        if (refundAmount != null && refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            Order order = orderRepo.findById(d.getOrderId()).orElse(null);
            if (order != null) {
                User buyer = userRepo.findById(order.getBuyerId()).orElse(null);
                if (buyer != null) {
                    buyer.setBalance(buyer.getBalance().add(refundAmount));
                    userRepo.save(buyer);
                    transactionRepo.save(new Transaction(buyer.getId(), refundAmount, "refund",
                        "Refund for dispute #" + id));
                }
            }
        }
        return d;
    }

    public List<Payout> getPayouts() {
        return payoutRepo.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Payout processPayout(Long id, String status, Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Payout p = payoutRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Payout not found"));
        p.setStatus(status);
        p.setProcessedBy(principal.getId());
        p.setUpdatedAt(LocalDateTime.now());
        payoutRepo.save(p);

        if ("completed".equals(status)) {
            User seller = userRepo.findById(p.getUserId()).orElse(null);
            if (seller != null) {
                seller.setBalance(seller.getBalance().subtract(p.getAmount()));
                userRepo.save(seller);
            }
        }
        return p;
    }

    public List<Product> getListings() {
        return productRepo.findAll();
    }

    @Transactional
    public void suspendListing(Long id, boolean suspend) {
        Product p = productRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        p.setIsActive(!suspend);
        p.setUpdatedAt(LocalDateTime.now());
        productRepo.save(p);
    }
}
