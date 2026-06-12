package com.kampuskart.controller;

import com.kampuskart.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MetaController {
    private final ProductService productService;

    public MetaController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(Map.of("categories", productService.getCategories()));
    }

    @GetMapping("/campuses")
    public ResponseEntity<?> getCampuses() {
        List<Map<String, String>> campuses = List.of(
            Map.of("id", "makerere", "name", "Makerere University"),
            Map.of("id", "kyambogo", "name", "Kyambogo University"),
            Map.of("id", "muk", "name", "Makerere University Business School"),
            Map.of("id", "ucu", "name", "Uganda Christian University"),
            Map.of("id", "isu", "name", "Islamic University in Uganda"),
            Map.of("id", "gu", "name", "Gulu University"),
            Map.of("id", "must", "name", "Mbarara University"),
            Map.of("id", "kabu", "name", "Kabale University"),
            Map.of("id", "umu", "name", "Uganda Martyrs University"),
            Map.of("id", "nkumba", "name", "Nkumba University")
        );
        return ResponseEntity.ok(Map.of("campuses", campuses));
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
            "status", "ok",
            "timestamp", LocalDateTime.now().toString()
        ));
    }
}
