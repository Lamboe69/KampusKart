package com.kampuskart.dto;

import com.kampuskart.entity.Order;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class OrderDto {
    private Long id;
    private Long buyerId;
    private Long sellerId;
    private Long productId;
    private Integer quantity;
    private BigDecimal total;
    private BigDecimal deliveryFee;
    private String status;
    private String deliveryAddress;
    private String deliveryCampus;
    private String paymentMethod;
    private String buyerName;
    private String sellerName;
    private String productTitle;
    private String productImage;
    private String createdAt;

    public static OrderDto from(Order o) {
        OrderDto d = new OrderDto();
        d.id = o.getId();
        d.buyerId = o.getBuyerId();
        d.sellerId = o.getSellerId();
        d.productId = o.getProductId();
        d.quantity = o.getQuantity();
        d.total = o.getTotal();
        d.deliveryFee = o.getDeliveryFee();
        d.status = o.getStatus();
        d.deliveryAddress = o.getDeliveryAddress();
        d.deliveryCampus = o.getDeliveryCampus();
        d.paymentMethod = o.getPaymentMethod();
        d.buyerName = o.getBuyerName();
        d.sellerName = o.getSellerName();
        d.productTitle = o.getProductTitle();
        d.productImage = o.getProductImage();
        d.createdAt = o.getCreatedAt() != null ? o.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
        return d;
    }

    public Long getId() { return id; }
    public Long getBuyerId() { return buyerId; }
    public Long getSellerId() { return sellerId; }
    public Long getProductId() { return productId; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getTotal() { return total; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public String getStatus() { return status; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getDeliveryCampus() { return deliveryCampus; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getBuyerName() { return buyerName; }
    public String getSellerName() { return sellerName; }
    public String getProductTitle() { return productTitle; }
    public String getProductImage() { return productImage; }
    public String getCreatedAt() { return createdAt; }
}
