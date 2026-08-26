package com.kampuskart.controller;

import com.kampuskart.service.ReviewService;
import com.kampuskart.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<?> create(Authentication auth, @RequestBody Map<String, Object> body) {
        try {
            Long productId = Long.valueOf(body.get("product_id").toString());
            Integer rating = Integer.valueOf(body.get("rating").toString());
            String comment = body.get("comment") != null ? body.get("comment").toString() : null;
            String orderId = body.get("order_id") != null ? body.get("order_id").toString() : null;
            return ResponseEntity.ok(reviewService.create(auth, productId, rating, comment, orderId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
