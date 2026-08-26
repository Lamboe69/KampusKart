package com.kampuskart.controller;

import com.kampuskart.dto.CreateProductRequest;
import com.kampuskart.entity.User;
import com.kampuskart.repository.UserRepository;
import com.kampuskart.security.UserPrincipal;
import com.kampuskart.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final UserRepository userRepo;

    public ProductController(ProductService productService, UserRepository userRepo) {
        this.productService = productService;
        this.userRepo = userRepo;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String campus,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String seller_id,
            @RequestParam(required = false) String sort) {
        List<?> products = productService.list(category, campus, search, minPrice, maxPrice, seller_id, sort);
        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        result.put("count", products.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(Authentication auth, @RequestBody CreateProductRequest req) {
        try {
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
                User user = userRepo.findById(principal.getId()).orElse(null);
                if (user != null) {
                    req.setSellerId(user.getId());
                    req.setSellerName(user.getName());
                }
            }
            return ResponseEntity.ok(productService.create(req.toProduct()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CreateProductRequest req) {
        try {
            return ResponseEntity.ok(productService.update(id, req.toProduct()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            productService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
