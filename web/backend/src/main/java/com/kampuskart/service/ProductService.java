package com.kampuskart.service;

import com.kampuskart.dto.ProductDto;
import com.kampuskart.entity.Product;
import com.kampuskart.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public List<ProductDto> list(String category, String campus, String search,
                                  BigDecimal minPrice, BigDecimal maxPrice,
                                  String sellerId, String sort) {
        List<Product> products = productRepo.findFiltered(
            category, campus, search, minPrice, maxPrice, sellerId);

        if ("price_asc".equals(sort) || "price-low".equals(sort))
            products.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
        else if ("price_desc".equals(sort) || "price-high".equals(sort))
            products.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
        else if ("rating".equals(sort))
            products.sort((a, b) -> b.getRating().compareTo(a.getRating()));
        else if ("newest".equals(sort))
            products.sort((a, b) -> {
                if (a.getCreatedAt() == null) return 1;
                if (b.getCreatedAt() == null) return -1;
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            });

        return products.stream().map(ProductDto::from).collect(Collectors.toList());
    }

    public ProductDto getById(Long id) {
        Product p = productRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductDto.from(p);
    }

    public ProductDto create(Product p) {
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        if (p.getIsActive() == null) p.setIsActive(true);
        if (p.getRating() == null) p.setRating(BigDecimal.ZERO);
        if (p.getReviewsCount() == null) p.setReviewsCount(0);
        if (p.getSalesCount() == null) p.setSalesCount(0);
        return ProductDto.from(productRepo.save(p));
    }

    public ProductDto update(Long id, Product updated) {
        Product p = productRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        if (updated.getTitle() != null) p.setTitle(updated.getTitle());
        if (updated.getDescription() != null) p.setDescription(updated.getDescription());
        if (updated.getPrice() != null) p.setPrice(updated.getPrice());
        if (updated.getOriginalPrice() != null) p.setOriginalPrice(updated.getOriginalPrice());
        if (updated.getDeliveryFee() != null) p.setDeliveryFee(updated.getDeliveryFee());
        if (updated.getDeliveryZones() != null) p.setDeliveryZones(updated.getDeliveryZones());
        if (updated.getDeliveryFees() != null) p.setDeliveryFees(updated.getDeliveryFees());
        if (updated.getCategory() != null) p.setCategory(updated.getCategory());
        if (updated.getCampus() != null) p.setCampus(updated.getCampus());
        if (updated.getCondition() != null) p.setCondition(updated.getCondition());
        if (updated.getImage() != null) p.setImage(updated.getImage());
        if (updated.getImages() != null) p.setImages(updated.getImages());
        if (updated.getSellerName() != null) p.setSellerName(updated.getSellerName());
        if (updated.getSellerType() != null) p.setSellerType(updated.getSellerType());
        if (updated.getBadge() != null) p.setBadge(updated.getBadge());
        if (updated.getReturnPolicy() != null) p.setReturnPolicy(updated.getReturnPolicy());
        p.setUpdatedAt(LocalDateTime.now());
        return ProductDto.from(productRepo.save(p));
    }

    public void delete(Long id) {
        Product p = productRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        p.setIsActive(false);
        p.setUpdatedAt(LocalDateTime.now());
        productRepo.save(p);
    }

    public List<String> getCategories() {
        return productRepo.findDistinctCategoryByIsActiveTrue();
    }
}
