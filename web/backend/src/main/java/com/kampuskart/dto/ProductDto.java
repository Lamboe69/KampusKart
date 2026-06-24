package com.kampuskart.dto;

import com.kampuskart.entity.Product;
import java.math.BigDecimal;

public class ProductDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal deliveryFee;
    private String deliveryZones;
    private String category;
    private String campus;
    private String condition;
    private String image;
    private String sellerName;
    private String sellerType;
    private BigDecimal rating;
    private Integer reviewsCount;
    private Integer salesCount;
    private Boolean isActive;
    private String createdAt;

    public static ProductDto from(Product p) {
        ProductDto d = new ProductDto();
        d.id = p.getId();
        d.title = p.getTitle();
        d.description = p.getDescription();
        d.price = p.getPrice();
        d.originalPrice = p.getOriginalPrice();
        d.deliveryFee = p.getDeliveryFee();
        d.deliveryZones = p.getDeliveryZones();
        d.category = p.getCategory();
        d.campus = p.getCampus();
        d.condition = p.getCondition();
        d.image = p.getImage();
        d.sellerName = p.getSellerName();
        d.sellerType = p.getSellerType();
        d.rating = p.getRating();
        d.reviewsCount = p.getReviewsCount();
        d.salesCount = p.getSalesCount();
        d.isActive = p.getIsActive();
        d.createdAt = p.getCreatedAt() != null ? p.getCreatedAt().toString() : null;
        return d;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public String getDeliveryZones() { return deliveryZones; }
    public String getCategory() { return category; }
    public String getCampus() { return campus; }
    public String getCondition() { return condition; }
    public String getImage() { return image; }
    public String getSellerName() { return sellerName; }
    public String getSellerType() { return sellerType; }
    public BigDecimal getRating() { return rating; }
    public Integer getReviewsCount() { return reviewsCount; }
    public Integer getSalesCount() { return salesCount; }
    public Boolean getIsActive() { return isActive; }
    public String getCreatedAt() { return createdAt; }
}
