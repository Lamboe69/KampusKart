package com.kampuskart.dto;

import com.kampuskart.entity.User;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class ShopDto {
    private String id;
    private String name;
    private String email;
    private String description;
    private String campus;
    private String image;
    private String phone;
    private String sellerType;
    private BigDecimal rating;
    private Integer reviewsCount;
    private Integer productsCount;
    private Integer salesCount;
    private Boolean verified;
    private String since;

    public static ShopDto from(User u, int productCount, int salesCount) {
        ShopDto d = new ShopDto();
        d.id = u.getId();
        d.name = u.getName();
        d.email = u.getEmail();
        d.description = u.getDescription();
        d.campus = u.getCampus();
        d.image = u.getImage();
        d.phone = u.getPhone();
        d.sellerType = u.getSellerType() != null ? u.getSellerType() : u.getRole();
        d.rating = u.getRating() != null ? u.getRating() : BigDecimal.ZERO;
        d.reviewsCount = u.getReviewsCount() != null ? u.getReviewsCount() : 0;
        d.productsCount = productCount;
        d.salesCount = salesCount;
        d.verified = u.getVerified() != null && u.getVerified();
        d.since = u.getCreatedAt() != null
            ? u.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy")) : "2024";
        return d;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDescription() { return description; }
    public String getCampus() { return campus; }
    public String getImage() { return image; }
    public String getPhone() { return phone; }
    public String getSellerType() { return sellerType; }
    public BigDecimal getRating() { return rating; }
    public Integer getReviewsCount() { return reviewsCount; }
    public Integer getProductsCount() { return productsCount; }
    public Integer getSalesCount() { return salesCount; }
    public Boolean getVerified() { return verified; }
    public String getSince() { return since; }
}
