package com.kampuskart.repository;

import com.kampuskart.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerIdOrderByCreatedAtDesc(String buyerId);
    List<Order> findBySellerIdOrderByCreatedAtDesc(String sellerId);
    long countByStatus(String status);
    long countBySellerIdAndStatus(String sellerId, String status);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status = 'completed'")
    java.math.BigDecimal getTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.sellerId = :sellerId AND o.status = 'completed'")
    long countCompletedBySellerId(@Param("sellerId") String sellerId);
}
