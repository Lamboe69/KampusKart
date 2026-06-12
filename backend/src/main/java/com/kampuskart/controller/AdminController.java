package com.kampuskart.controller;

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
        return ResponseEntity.ok(adminService.updateVerification(id, body.get("status"), body.get("adminNote")));
    }

    @GetMapping("/disputes")
    public ResponseEntity<?> getDisputes() {
        return ResponseEntity.ok(adminService.getDisputes());
    }

    @PutMapping("/disputes/{id}")
    public ResponseEntity<?> resolveDispute(@PathVariable Long id, @RequestBody Map<String, Object> body,
                                             Authentication auth) {
        BigDecimal refund = body.get("refundAmount") != null
            ? BigDecimal.valueOf(((Number) body.get("refundAmount")).doubleValue()) : null;
        return ResponseEntity.ok(adminService.resolveDispute(id, (String) body.get("status"),
            (String) body.get("resolutionNote"), refund, auth));
    }

    @GetMapping("/payouts")
    public ResponseEntity<?> getPayouts() {
        return ResponseEntity.ok(adminService.getPayouts());
    }

    @PutMapping("/payouts/{id}")
    public ResponseEntity<?> processPayout(@PathVariable Long id, @RequestBody Map<String, String> body,
                                            Authentication auth) {
        return ResponseEntity.ok(adminService.processPayout(id, body.get("status"), auth));
    }

    @GetMapping("/listings")
    public ResponseEntity<?> getListings() {
        return ResponseEntity.ok(adminService.getListings());
    }

    @PutMapping("/listings/{id}/suspend")
    public ResponseEntity<?> suspendListing(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean suspend = Boolean.TRUE.equals(body.get("suspend"));
        adminService.suspendListing(id, suspend);
        return ResponseEntity.ok(Map.of("message", suspend ? "Listing suspended" : "Listing reactivated"));
    }
}
