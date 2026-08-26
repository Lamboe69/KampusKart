package com.kampuskart.controller;

import com.kampuskart.entity.Product;
import com.kampuskart.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MetaController {
    private final ProductRepository productRepo;

    public MetaController(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        List<Map<String, String>> categories = List.of(
            Map.of("id", "electronics", "name", "Electronics", "icon", "💻"),
            Map.of("id", "fashion", "name", "Fashion & Clothing", "icon", "👕"),
            Map.of("id", "books", "name", "Books & Stationery", "icon", "📚"),
            Map.of("id", "food", "name", "Food & Snacks", "icon", "🍕"),
            Map.of("id", "beauty", "name", "Beauty & Health", "icon", "💄"),
            Map.of("id", "services", "name", "Services", "icon", "🔧"),
            Map.of("id", "furniture", "name", "Furniture & Home", "icon", "🪑"),
            Map.of("id", "vehicles", "name", "Vehicles", "icon", "🚗")
        );
        return ResponseEntity.ok(Map.of("categories", categories));
    }

    @GetMapping("/campuses")
    public ResponseEntity<?> getCampuses() {
        List<Map<String, String>> campuses = List.of(
            Map.of("id", "makerere", "name", "Makerere University", "location", "Kampala"),
            Map.of("id", "kyambogo", "name", "Kyambogo University", "location", "Kampala"),
            Map.of("id", "muk", "name", "Makerere University Business School", "location", "Kampala"),
            Map.of("id", "ucu", "name", "Uganda Christian University", "location", "Mukono"),
            Map.of("id", "gu", "name", "Gulu University", "location", "Gulu"),
            Map.of("id", "must", "name", "Mbarara University of Science and Technology", "location", "Mbarara"),
            Map.of("id", "busitema", "name", "Busitema University", "location", "Busitema")
        );
        return ResponseEntity.ok(Map.of("campuses", campuses));
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "timestamp", System.currentTimeMillis()));
    }
}
