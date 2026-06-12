package com.kampuskart.dto;

import com.kampuskart.entity.User;
import java.math.BigDecimal;

public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String role;
    private String sellerType;
    private String description;
    private String campus;
    private String image;
    private String phone;
    private BigDecimal balance;
    private BigDecimal pendingBalance;
    private BigDecimal totalEarned;
    private BigDecimal rating;
    private Integer reviewsCount;
    private Integer productsCount;
    private Integer salesCount;
    private Boolean verified;
    private Boolean isActive;

    public static UserDto from(User u) {
        UserDto d = new UserDto();
        d.id = u.getId();
        d.email = u.getEmail();
        d.name = u.getName();
        d.role = u.getRole();
        d.sellerType = u.getSellerType();
        d.description = u.getDescription();
        d.campus = u.getCampus();
        d.image = u.getImage();
        d.phone = u.getPhone();
        d.balance = u.getBalance();
        d.pendingBalance = u.getPendingBalance();
        d.totalEarned = u.getTotalEarned();
        d.rating = u.getRating();
        d.reviewsCount = u.getReviewsCount();
        d.productsCount = u.getProductsCount();
        d.salesCount = u.getSalesCount();
        d.verified = u.getVerified();
        d.isActive = u.getIsActive();
        return d;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getSellerType() { return sellerType; }
    public String getDescription() { return description; }
    public String getCampus() { return campus; }
    public String getImage() { return image; }
    public String getPhone() { return phone; }
    public BigDecimal getBalance() { return balance; }
    public BigDecimal getPendingBalance() { return pendingBalance; }
    public BigDecimal getTotalEarned() { return totalEarned; }
    public BigDecimal getRating() { return rating; }
    public Integer getReviewsCount() { return reviewsCount; }
    public Integer getProductsCount() { return productsCount; }
    public Integer getSalesCount() { return salesCount; }
    public Boolean getVerified() { return verified; }
    public Boolean getIsActive() { return isActive; }
}
