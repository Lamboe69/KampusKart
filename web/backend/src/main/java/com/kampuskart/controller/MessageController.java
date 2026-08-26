package com.kampuskart.controller;

import com.kampuskart.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(Authentication auth) {
        return ResponseEntity.ok(messageService.getConversations(auth));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getMessages(Authentication auth, @PathVariable String userId) {
        return ResponseEntity.ok(messageService.getMessages(auth, userId));
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(Authentication auth, @RequestBody Map<String, String> body) {
        try {
            String receiverId = body.get("receiver_id");
            String message = body.get("message");
            String image = body.get("image");
            return ResponseEntity.ok(messageService.sendMessage(auth, receiverId, message, image));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
