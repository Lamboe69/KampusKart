package com.kampuskart.repository;

import com.kampuskart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsActiveTrueAndSellerName(String sellerName);

    @Query(value = "SELECT * FROM products p WHERE p.is_active = true " +
           "AND (CAST(?1 AS varchar) IS NULL OR ?1 = 'all' OR p.category = CAST(?1 AS varchar)) " +
           "AND (CAST(?2 AS varchar) IS NULL OR ?2 = 'all' OR p.campus = CAST(?2 AS varchar)) " +
           "AND (CAST(?3 AS varchar) IS NULL OR p.title ILIKE '%' || CAST(?3 AS varchar) || '%' OR p.seller_name ILIKE '%' || CAST(?3 AS varchar) || '%') " +
           "AND (CAST(?4 AS numeric) IS NULL OR p.price >= CAST(?4 AS numeric)) " +
           "AND (CAST(?5 AS numeric) IS NULL OR p.price <= CAST(?5 AS numeric)) " +
           "AND (CAST(?6 AS bigint) IS NULL OR p.seller_name IN (SELECT u.name FROM users u WHERE u.id = CAST(?6 AS bigint)))",
           nativeQuery = true)
    List<Product> findFiltered(String category, String campus, String search,
                               BigDecimal minPrice, BigDecimal maxPrice, Long sellerId);

    long countByCategoryAndIsActiveTrue(String category);
    List<Product> findBySellerName(String sellerName);
    List<String> findDistinctCategoryByIsActiveTrue();
}
