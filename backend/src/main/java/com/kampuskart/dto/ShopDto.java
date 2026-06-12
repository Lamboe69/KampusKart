package com.kampuskart.dto;

import java.math.BigDecimal;

public class ShopDto {
    private Long id;
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSellerType() { return sellerType; }
    public void setSellerType(String sellerType) { this.sellerType = sellerType; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public Integer getReviewsCount() { return reviewsCount; }
    public void setReviewsCount(Integer reviewsCount) { this.reviewsCount = reviewsCount; }
    public Integer getProductsCount() { return productsCount; }
    public void setProductsCount(Integer productsCount) { this.productsCount = productsCount; }
    public Integer getSalesCount() { return salesCount; }
    public void setSalesCount(Integer salesCount) { this.salesCount = salesCount; }
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    public String getSince() { return since; }
    public void setSince(String since) { this.since = since; }
}
