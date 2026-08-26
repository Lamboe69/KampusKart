package com.kampuskart.controller;

import com.kampuskart.entity.Product;
import com.kampuskart.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final ProductRepository productRepo;

    public SearchController(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @GetMapping("/suggestions")
    public ResponseEntity<?> getSuggestions(@RequestParam(defaultValue = "") String q) {
        if (q.isEmpty()) {
            return ResponseEntity.ok(Map.of("suggestions", List.of(), "categories", List.of()));
        }

        String term = q.toLowerCase();
        List<Product> matches = productRepo.searchByTerm(term);

        List<String> suggestions = matches.stream()
            .map(Product::getTitle)
            .distinct()
            .limit(8)
            .collect(Collectors.toList());

        List<String> categories = matches.stream()
            .map(Product::getCategory)
            .filter(Objects::nonNull)
            .distinct()
            .limit(4)
            .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("suggestions", suggestions, "categories", categories));
    }
}
