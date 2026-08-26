package com.kampuskart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.kampuskart.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CreateProductRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String category;
    private String campus;
    private String condition;
    private String image;

    @JsonProperty("images")
    private List<String> imagesList;

    private String deliveryZone;

    @JsonProperty("delivery_zones")
    private List<String> deliveryZonesList;

    @JsonProperty("delivery_fee")
    private BigDecimal deliveryFee;

    @JsonProperty("delivery_fees")
    private Map<String, Object> deliveryFeesMap;

    @JsonProperty("return_policy")
    private String returnPolicy;

    @JsonProperty("seller_id")
    private String sellerId;

    @JsonProperty("seller_name")
    private String sellerName;

    public Product toProduct() {
        Product p = new Product();
        p.setTitle(title);
        p.setDescription(description);
        p.setPrice(price);
        p.setOriginalPrice(originalPrice);
        p.setCategory(category);
        p.setCampus(campus);
        p.setCondition(condition);
        p.setImage(image);
        p.setDeliveryFee(deliveryFee != null ? deliveryFee : BigDecimal.ZERO);
        p.setReturnPolicy(returnPolicy);

        if (imagesList != null && !imagesList.isEmpty()) {
            String joined = String.join(",", imagesList);
            p.setImages("[" + imagesList.stream().map(s -> "\"" + s.replace("\"", "\\\"") + "\"").reduce((a, b) -> a + "," + b).orElse("") + "]");
            if (p.getImage() == null || p.getImage().isEmpty()) {
                p.setImage(imagesList.get(0));
            }
        }

        if (deliveryZonesList != null && !deliveryZonesList.isEmpty()) {
            p.setDeliveryZones("[" + deliveryZonesList.stream().map(s -> "\"" + s.replace("\"", "\\\"") + "\"").reduce((a, b) -> a + "," + b).orElse("") + "]");
        }

        if (deliveryFeesMap != null) {
            StringBuilder sb = new StringBuilder("{");
            deliveryFeesMap.forEach((k, v) -> {
                if (sb.length() > 1) sb.append(",");
                sb.append("\"").append(k.replace("\"", "\\\"")).append("\":").append(v);
            });
            sb.append("}");
            p.setDeliveryFees(sb.toString());
        }

        if (sellerId == null || sellerId.isEmpty()) {
            p.setSellerId("unknown");
        } else {
            p.setSellerId(sellerId);
        }
        p.setSellerName(sellerName);

        return p;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public List<String> getImagesList() { return imagesList; }
    public void setImagesList(List<String> imagesList) { this.imagesList = imagesList; }
    public List<String> getDeliveryZonesList() { return deliveryZonesList; }
    public void setDeliveryZonesList(List<String> deliveryZonesList) { this.deliveryZonesList = deliveryZonesList; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }
    public Map<String, Object> getDeliveryFeesMap() { return deliveryFeesMap; }
    public void setDeliveryFeesMap(Map<String, Object> deliveryFeesMap) { this.deliveryFeesMap = deliveryFeesMap; }
    public String getReturnPolicy() { return returnPolicy; }
    public void setReturnPolicy(String returnPolicy) { this.returnPolicy = returnPolicy; }
    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
}
