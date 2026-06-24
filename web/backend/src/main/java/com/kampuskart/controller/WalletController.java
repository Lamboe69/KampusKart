package com.kampuskart.controller;

import com.kampuskart.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public ResponseEntity<?> getWallet(Authentication auth) {
        return ResponseEntity.ok(walletService.getWallet(auth));
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(Authentication auth) {
        return ResponseEntity.ok(walletService.getTransactions(auth));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(Authentication auth, @RequestBody Map<String, Object> body) {
        try {
            BigDecimal amount = BigDecimal.valueOf(((Number) body.get("amount")).doubleValue());
            walletService.withdraw(auth, amount);
            return ResponseEntity.ok(Map.of("message", "Withdrawal successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/topup")
    public ResponseEntity<?> topup(Authentication auth, @RequestBody Map<String, Object> body) {
        try {
            BigDecimal amount = BigDecimal.valueOf(((Number) body.get("amount")).doubleValue());
            walletService.topup(auth, amount);
            return ResponseEntity.ok(Map.of("message", "Top up successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
