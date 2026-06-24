package com.kampuskart.service;

import com.kampuskart.entity.Notification;
import com.kampuskart.repository.NotificationRepository;
import com.kampuskart.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepo;

    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    public List<Map<String, Object>> list(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(principal.getId())
            .stream().map(n -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", n.getId());
                m.put("title", n.getTitle());
                m.put("message", n.getMessage());
                m.put("type", n.getType());
                m.put("isRead", n.getIsRead());
                m.put("createdAt", n.getCreatedAt().toString());
                return m;
            }).collect(Collectors.toList());
    }

    public void markRead(Long id) {
        Notification n = notificationRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setIsRead(true);
        notificationRepo.save(n);
    }

    @Transactional
    public void markAllRead(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        notificationRepo.markAllReadByUserId(principal.getId());
    }
}
