package com.kampuskart.controller;

import com.kampuskart.entity.*;
import com.kampuskart.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/verifications")
    public ResponseEntity<?> getVerifications() {
        return ResponseEntity.ok(adminService.getVerifications());
    }

    @PutMapping("/verifications/{id}")
    public ResponseEntity<?> updateVerification(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(adminService.updateVerification(id, body.get("status"), body.get("adminNote")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/disputes")
    public ResponseEntity<?> getDisputes() {
        return ResponseEntity.ok(adminService.getDisputes());
    }

    @PutMapping("/disputes/{id}")
    public ResponseEntity<?> resolveDispute(@PathVariable Long id, @RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String status = (String) body.get("status");
            String note = body.get("resolution") != null ? body.get("resolution").toString() :
                          body.get("resolutionNote") != null ? body.get("resolutionNote").toString() : null;
            BigDecimal refundAmount = body.get("refundAmount") != null ?
                new BigDecimal(body.get("refundAmount").toString()) : null;
            return ResponseEntity.ok(adminService.resolveDispute(id, status, note, refundAmount, auth));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/payouts")
    public ResponseEntity<?> getPayouts() {
        return ResponseEntity.ok(adminService.getPayouts());
    }

    @PutMapping("/payouts/{id}")
    public ResponseEntity<?> processPayout(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication auth) {
        try {
            return ResponseEntity.ok(adminService.processPayout(id, body.get("status"), auth));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/listings")
    public ResponseEntity<?> getListings() {
        return ResponseEntity.ok(adminService.getListings());
    }

    @PutMapping("/listings/{id}/suspend")
    public ResponseEntity<?> suspendListing(@PathVariable Long id) {
        try {
            adminService.suspendListing(id);
            return ResponseEntity.ok(Map.of("message", "Listing status toggled"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
