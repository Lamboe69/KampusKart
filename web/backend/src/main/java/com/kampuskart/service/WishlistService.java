package com.kampuskart.service;

import com.kampuskart.dto.ProductDto;
import com.kampuskart.entity.Wishlist;
import com.kampuskart.repository.ProductRepository;
import com.kampuskart.repository.WishlistRepository;
import com.kampuskart.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WishlistService {
    private final WishlistRepository wishlistRepo;
    private final ProductRepository productRepo;

    public WishlistService(WishlistRepository wishlistRepo, ProductRepository productRepo) {
        this.wishlistRepo = wishlistRepo;
        this.productRepo = productRepo;
    }

    public List<Map<String, Object>> getWishlist(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return wishlistRepo.findByUserIdOrderByCreatedAtDesc(principal.getId()).stream()
            .map(w -> {
                var product = productRepo.findById(w.getProductId()).orElse(null);
                if (product == null) return null;
                Map<String, Object> m = new HashMap<>();
                m.put("id", product.getId());
                m.put("title", product.getTitle());
                m.put("price", product.getPrice());
                m.put("image", product.getImage());
                m.put("category", product.getCategory());
                m.put("campus", product.getCampus());
                m.put("sellerName", product.getSellerName());
                m.put("rating", product.getRating());
                return m;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public List<Long> getWishlistIds(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return wishlistRepo.findByUserIdOrderByCreatedAtDesc(principal.getId()).stream()
            .map(Wishlist::getProductId)
            .collect(Collectors.toList());
    }

    @Transactional
    public void addToWishlist(Authentication auth, Long productId) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        if (!wishlistRepo.existsByUserIdAndProductId(principal.getId(), productId)) {
            Wishlist w = new Wishlist();
            w.setUserId(principal.getId());
            w.setProductId(productId);
            w.setCreatedAt(LocalDateTime.now());
            wishlistRepo.save(w);
        }
    }

    @Transactional
    public void removeFromWishlist(Authentication auth, Long productId) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        wishlistRepo.deleteByUserIdAndProductId(principal.getId(), productId);
    }
}
