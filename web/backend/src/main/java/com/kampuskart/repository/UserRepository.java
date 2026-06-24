package com.kampuskart.repository;

import com.kampuskart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByName(String name);
    boolean existsByEmail(String email);
    List<User> findByRoleAndIsActiveTrue(String role);
    List<User> findByRoleAndIsActiveTrueAndCampus(String role, String campus);
    long countByRole(String role);

    @Query("SELECT u FROM User u WHERE u.role IN ('seller','shop') AND u.isActive = true")
    List<User> findAllSellers();

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.sellerId = :sellerId AND o.status = 'completed'")
    java.math.BigDecimal getSellerRevenue(Long sellerId);
}
