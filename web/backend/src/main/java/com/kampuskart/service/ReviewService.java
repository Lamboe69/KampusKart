package com.kampuskart.service;

import com.kampuskart.entity.Product;
import com.kampuskart.entity.Review;
import com.kampuskart.repository.ProductRepository;
import com.kampuskart.repository.ReviewRepository;
import com.kampuskart.repository.UserRepository;
import com.kampuskart.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public ReviewService(ReviewRepository reviewRepo, ProductRepository productRepo, UserRepository userRepo) {
        this.reviewRepo = reviewRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    public Map<String, Object> getByProductId(Long productId) {
        List<Review> reviews = reviewRepo.findByProductIdOrderByCreatedAtDesc(productId);
        double avgRating = reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);

        List<Map<String, Object>> reviewList = reviews.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", r.getId());
            m.put("productId", r.getProductId());
            m.put("userId", r.getUserId());
            m.put("userName", r.getUserName());
            m.put("rating", r.getRating());
            m.put("comment", r.getComment());
            m.put("createdAt", r.getCreatedAt() != null ? r.getCreatedAt().toString() : null);
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("reviews", reviewList);
        result.put("averageRating", Math.round(avgRating * 10.0) / 10.0);
        result.put("totalReviews", reviews.size());
        return result;
    }

    @Transactional
    public Review create(Authentication auth, Long productId, Integer rating, String comment, String orderId) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String userId = principal.getId();

        var user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Review review = new Review();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setUserName(user.getName());
        review.setRating(rating);
        review.setComment(comment);
        review.setOrderId(orderId);
        review.setCreatedAt(LocalDateTime.now());
        review = reviewRepo.save(review);

        List<Review> allReviews = reviewRepo.findByProductIdOrderByCreatedAtDesc(productId);
        double avg = allReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

        Product product = productRepo.findById(productId).orElse(null);
        if (product != null) {
            product.setRating(BigDecimal.valueOf(Math.round(avg * 10.0) / 10.0));
            product.setReviewsCount(allReviews.size());
            productRepo.save(product);
        }

        return review;
    }
}
