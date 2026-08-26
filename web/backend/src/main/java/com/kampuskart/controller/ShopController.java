package com.kampuskart.controller;

import com.kampuskart.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shops")
public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) String campus,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search) {
        var shops = shopService.list(campus, type, search);
        return ResponseEntity.ok(Map.of("shops", shops, "count", shops.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(shopService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
