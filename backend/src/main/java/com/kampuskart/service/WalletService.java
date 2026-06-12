package com.kampuskart.service;

import com.kampuskart.entity.Transaction;
import com.kampuskart.entity.User;
import com.kampuskart.repository.TransactionRepository;
import com.kampuskart.repository.UserRepository;
import com.kampuskart.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WalletService {
    private final UserRepository userRepo;
    private final TransactionRepository transactionRepo;

    public WalletService(UserRepository userRepo, TransactionRepository transactionRepo) {
        this.userRepo = userRepo;
        this.transactionRepo = transactionRepo;
    }

    public Map<String, Object> getWallet(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepo.findById(principal.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, Object> result = new HashMap<>();
        result.put("balance", user.getBalance());
        result.put("pending", user.getPendingBalance());
        result.put("totalEarned", user.getTotalEarned());
        return result;
    }

    public List<Map<String, Object>> getTransactions(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return transactionRepo.findByUserIdOrderByCreatedAtDesc(principal.getId())
            .stream().map(t -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", t.getId());
                m.put("amount", t.getAmount());
                m.put("type", t.getType());
                m.put("status", t.getStatus());
                m.put("description", t.getDescription());
                m.put("createdAt", t.getCreatedAt().toString());
                return m;
            }).collect(Collectors.toList());
    }

    @Transactional
    public void withdraw(Authentication auth, BigDecimal amount) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepo.findById(principal.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (amount.compareTo(BigDecimal.valueOf(10000)) < 0) {
            throw new RuntimeException("Minimum withdrawal is UGX 10,000");
        }
        if (user.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        user.setBalance(user.getBalance().subtract(amount));
        userRepo.save(user);
        transactionRepo.save(new Transaction(user.getId(), amount.negate(), "withdrawal",
            "Withdrawal of UGX " + amount));
    }

    @Transactional
    public void topup(Authentication auth, BigDecimal amount) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepo.findById(principal.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBalance(user.getBalance().add(amount));
        userRepo.save(user);
        transactionRepo.save(new Transaction(user.getId(), amount, "topup",
            "Top up of UGX " + amount));
    }
}
