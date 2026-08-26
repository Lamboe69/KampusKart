package com.kampuskart.service;

import com.kampuskart.entity.Message;
import com.kampuskart.entity.User;
import com.kampuskart.repository.MessageRepository;
import com.kampuskart.repository.UserRepository;
import com.kampuskart.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepo;
    private final UserRepository userRepo;

    public MessageService(MessageRepository messageRepo, UserRepository userRepo) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
    }

    public List<Map<String, Object>> getConversations(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String userId = principal.getId();
        List<Message> allMessages = messageRepo.findByUserId(userId);

        Set<String> partnerIds = new LinkedHashSet<>();
        for (Message m : allMessages) {
            if (!m.getSenderId().equals(userId)) partnerIds.add(m.getSenderId());
            if (!m.getReceiverId().equals(userId)) partnerIds.add(m.getReceiverId());
        }

        return partnerIds.stream().map(partnerId -> {
            User partner = userRepo.findById(partnerId).orElse(null);
            if (partner == null) return null;

            Message lastMsg = messageRepo.findLastMessage(userId, partnerId);
            long unreadCount = 0;
            if (lastMsg != null && lastMsg.getReceiverId().equals(userId) && !lastMsg.getRead()) {
                unreadCount = 1;
            }

            Map<String, Object> conv = new HashMap<>();
            conv.put("userId", partner.getId());
            conv.put("name", partner.getName());
            conv.put("image", partner.getImage());
            conv.put("lastMessage", lastMsg != null ? lastMsg.getMessage() : "");
            conv.put("lastMessageTime", lastMsg != null && lastMsg.getCreatedAt() != null ? lastMsg.getCreatedAt().toString() : null);
            conv.put("unreadCount", unreadCount);
            return conv;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Transactional
    public List<Map<String, Object>> getMessages(Authentication auth, String otherUserId) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String userId = principal.getId();
        messageRepo.markAsRead(otherUserId, userId);

        return messageRepo.findConversation(userId, otherUserId).stream().map(m -> {
            Map<String, Object> msg = new HashMap<>();
            msg.put("id", m.getId());
            msg.put("senderId", m.getSenderId());
            msg.put("receiverId", m.getReceiverId());
            msg.put("message", m.getMessage());
            msg.put("read", m.getRead());
            msg.put("image", m.getImage());
            msg.put("createdAt", m.getCreatedAt() != null ? m.getCreatedAt().toString() : null);
            return msg;
        }).collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> sendMessage(Authentication auth, String receiverId, String messageText, String image) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Message msg = new Message();
        msg.setSenderId(principal.getId());
        msg.setReceiverId(receiverId);
        msg.setMessage(messageText);
        msg.setImage(image);
        msg.setCreatedAt(LocalDateTime.now());
        msg = messageRepo.save(msg);

        Map<String, Object> result = new HashMap<>();
        result.put("id", msg.getId());
        result.put("senderId", msg.getSenderId());
        result.put("receiverId", msg.getReceiverId());
        result.put("message", msg.getMessage());
        result.put("read", msg.getRead());
        result.put("image", msg.getImage());
        result.put("createdAt", msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : null);
        return result;
    }
}
