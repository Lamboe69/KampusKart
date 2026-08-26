package com.kampuskart.repository;

import com.kampuskart.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Wishlist> findProductIdsByUserId(String userId);
    Optional<Wishlist> findByUserIdAndProductId(String userId, Long productId);
    boolean existsByUserIdAndProductId(String userId, Long productId);
    void deleteByUserIdAndProductId(String userId, Long productId);
}
