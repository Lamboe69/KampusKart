package com.kampuskart.controller;

import com.kampuskart.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<?> getWishlist(Authentication auth) {
        return ResponseEntity.ok(Map.of("items", wishlistService.getWishlist(auth)));
    }

    @GetMapping("/ids")
    public ResponseEntity<?> getWishlistIds(Authentication auth) {
        return ResponseEntity.ok(Map.of("ids", wishlistService.getWishlistIds(auth)));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<?> addToWishlist(Authentication auth, @PathVariable Long productId) {
        wishlistService.addToWishlist(auth, productId);
        return ResponseEntity.ok(Map.of("message", "Added to wishlist"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromWishlist(Authentication auth, @PathVariable Long productId) {
        wishlistService.removeFromWishlist(auth, productId);
        return ResponseEntity.ok(Map.of("message", "Removed from wishlist"));
    }
}
