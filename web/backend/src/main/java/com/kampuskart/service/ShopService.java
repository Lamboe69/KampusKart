package com.kampuskart.service;

import com.kampuskart.dto.ShopDto;
import com.kampuskart.dto.ProductDto;
import com.kampuskart.entity.User;
import com.kampuskart.repository.ProductRepository;
import com.kampuskart.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopService {
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    public ShopService(UserRepository userRepo, ProductRepository productRepo) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    public List<ShopDto> list(String campus) {
        List<User> sellers = campus != null && !"all".equals(campus)
            ? userRepo.findByRoleAndIsActiveTrueAndCampus("seller", campus)
            : userRepo.findByRoleAndIsActiveTrue("seller");
        List<User> shops = campus != null && !"all".equals(campus)
            ? userRepo.findByRoleAndIsActiveTrueAndCampus("shop", campus)
            : userRepo.findByRoleAndIsActiveTrue("shop");

        List<User> all = new ArrayList<>();
        all.addAll(sellers);
        all.addAll(shops);

        return all.stream().map(this::toShopDto).collect(Collectors.toList());
    }

    public Map<String, Object> getById(Long id) {
        User seller = userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Shop not found"));
        Map<String, Object> result = new HashMap<>();
        result.put("shop", toShopDto(seller));
        result.put("products", productRepo.findByIsActiveTrueAndSellerName(seller.getName())
            .stream().map(ProductDto::from).collect(Collectors.toList()));
        return result;
    }

    private ShopDto toShopDto(User u) {
        ShopDto d = new ShopDto();
        d.setId(u.getId());
        d.setName(u.getName());
        d.setEmail(u.getEmail());
        d.setDescription(u.getDescription());
        d.setCampus(u.getCampus());
        d.setImage(u.getImage());
        d.setPhone(u.getPhone());
        d.setSellerType(u.getSellerType() != null ? u.getSellerType() : u.getRole());
        d.setRating(u.getRating() != null ? u.getRating() : BigDecimal.ZERO);
        d.setReviewsCount(u.getReviewsCount() != null ? u.getReviewsCount() : 0);
        d.setProductsCount(u.getProductsCount() != null ? u.getProductsCount() : 0);
        d.setSalesCount(u.getSalesCount() != null ? u.getSalesCount() : 0);
        d.setVerified(u.getVerified() != null && u.getVerified());
        d.setSince(u.getCreatedAt() != null
            ? u.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy")) : "2024");
        return d;
    }
}
