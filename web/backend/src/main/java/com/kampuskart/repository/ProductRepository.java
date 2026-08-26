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

    @Query(value = "SELECT * FROM products p WHERE p.is_active = true " +
           "AND (:category IS NULL OR LOWER(p.category) = LOWER(:category)) " +
           "AND (:campus IS NULL OR p.campus = :campus) " +
           "AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:sellerId IS NULL OR p.seller_id = :sellerId)",
           nativeQuery = true)
    List<Product> findFiltered(@Param("category") String category,
                               @Param("campus") String campus,
                               @Param("search") String search,
                               @Param("minPrice") BigDecimal minPrice,
                               @Param("maxPrice") BigDecimal maxPrice,
                               @Param("sellerId") String sellerId);

    List<Product> findByIsActiveTrueAndSellerName(String sellerName);

    List<Product> findBySellerId(String sellerId);

    long countByIsActiveTrue();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.sellerId = :sellerId AND p.isActive = true")
    long countBySellerId(@Param("sellerId") String sellerId);

    @Query(value = "SELECT DISTINCT category FROM products WHERE is_active = true ORDER BY category", nativeQuery = true)
    List<String> findDistinctCategoryByIsActiveTrue();

    @Query(value = "SELECT DISTINCT category FROM products WHERE is_active = true", nativeQuery = true)
    List<String> findDistinctCategories();

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :term, '%')))")
    List<Product> searchByTerm(@Param("term") String term);
}
