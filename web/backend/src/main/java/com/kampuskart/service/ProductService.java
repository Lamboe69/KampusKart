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
                                  Long sellerId, String sort) {
        List<Product> products = productRepo.findFiltered(
            category, campus, search, minPrice, maxPrice, sellerId);

        if ("price_asc".equals(sort))
            products.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
        else if ("price_desc".equals(sort))
            products.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
        else if ("newest".equals(sort))
            products.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        else if ("oldest".equals(sort))
            products.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));

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
        if (updated.getCategory() != null) p.setCategory(updated.getCategory());
        if (updated.getCampus() != null) p.setCampus(updated.getCampus());
        if (updated.getCondition() != null) p.setCondition(updated.getCondition());
        if (updated.getImage() != null) p.setImage(updated.getImage());
        if (updated.getSellerName() != null) p.setSellerName(updated.getSellerName());
        if (updated.getSellerType() != null) p.setSellerType(updated.getSellerType());
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
