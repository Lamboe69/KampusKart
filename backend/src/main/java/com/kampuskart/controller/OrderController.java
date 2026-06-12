package com.kampuskart.controller;

import com.kampuskart.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> list(Authentication auth) {
        return ResponseEntity.ok(orderService.list(auth));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(Authentication auth, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.getById(auth, id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(Authentication auth, @RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(orderService.create(auth, body));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(Authentication auth, @PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(orderService.updateStatus(auth, id, body.get("status")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
