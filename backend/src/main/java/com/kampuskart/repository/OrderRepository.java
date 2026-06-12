package com.kampuskart.repository;

import com.kampuskart.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    List<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    long countByStatus(String status);
    long countBySellerIdAndStatus(Long sellerId, String status);
}
