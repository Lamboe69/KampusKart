package com.kampuskart.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kampuskart.entity.Message;
import com.kampuskart.entity.User;
import com.kampuskart.repository.MessageRepository;
import com.kampuskart.repository.UserRepository;
import com.kampuskart.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    private final JwtUtil jwtUtil;
    private final MessageRepository messageRepo;
    private final UserRepository userRepo;

    public ChatWebSocketHandler(JwtUtil jwtUtil, MessageRepository messageRepo, UserRepository userRepo) {
        this.jwtUtil = jwtUtil;
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        String token = null;
        if (query != null) {
            Map<String, String> params = UriComponentsBuilder.fromUriString("?" + query).build()
                .getQueryParams().toSingleValueMap();
            token = params.get("token");
        }

        if (token == null || !jwtUtil.validateToken(token)) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid token"));
            return;
        }

        String userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("User not found"));
            return;
        }

        clients.put(userId, session);
        sessionUserMap.put(session.getId(), userId);
        log.info("WebSocket connected: {} ({})", user.getName(), userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        String senderId = sessionUserMap.get(session.getId());
        if (senderId == null) return;

        JsonNode node = mapper.readTree(textMessage.getPayload());
        String type = node.has("type") ? node.get("type").asText() : "message";

        switch (type) {
            case "message" -> handleChatMessage(senderId, node);
            case "typing" -> handleTyping(senderId, node);
            case "read" -> handleReadReceipt(senderId, node);
            default -> log.warn("Unknown message type: {}", type);
        }
    }

    private void handleChatMessage(String senderId, JsonNode node) throws Exception {
        String receiverId = node.get("receiver_id").asText();
        String text = node.has("message") ? node.get("message").asText() : "";
        String image = node.has("image") ? node.get("image").asText() : null;

        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setMessage(text);
        msg.setImage(image);
        msg.setCreatedAt(LocalDateTime.now());
        msg = messageRepo.save(msg);

        ObjectNode msgJson = mapper.createObjectNode();
        msgJson.put("type", "new_message");
        msgJson.put("id", msg.getId());
        msgJson.put("senderId", msg.getSenderId());
        msgJson.put("receiverId", msg.getReceiverId());
        msgJson.put("message", msg.getMessage());
        msgJson.put("image", msg.getImage());
        msgJson.put("read", false);
        msgJson.put("createdAt", msg.getCreatedAt().toString());

        WebSocketSession senderSession = clients.get(senderId);
        WebSocketSession receiverSession = clients.get(receiverId);

        TextMessage payload = new TextMessage(mapper.writeValueAsString(msgJson));

        if (senderSession != null && senderSession.isOpen()) {
            ObjectNode ack = mapper.createObjectNode();
            ack.put("type", "sent");
            ack.put("id", msg.getId());
            ack.put("createdAt", msg.getCreatedAt().toString());
            senderSession.sendMessage(new TextMessage(mapper.writeValueAsString(ack)));
        }

        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(payload);
        }

        if (receiverId != null) {
            sendNotification(receiverId, "New message from " + userRepo.findById(senderId).map(User::getName).orElse("Someone"));
        }
    }

    private void handleTyping(String senderId, JsonNode node) throws Exception {
        String receiverId = node.get("receiver_id").asText();
        WebSocketSession receiverSession = clients.get(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            ObjectNode typingJson = mapper.createObjectNode();
            typingJson.put("type", "typing");
            typingJson.put("senderId", senderId);
            receiverSession.sendMessage(new TextMessage(mapper.writeValueAsString(typingJson)));
        }
    }

    private void handleReadReceipt(String senderId, JsonNode node) throws Exception {
        String otherUserId = node.get("sender_id").asText();
        messageRepo.markAsRead(otherUserId, senderId);

        WebSocketSession otherSession = clients.get(otherUserId);
        if (otherSession != null && otherSession.isOpen()) {
            ObjectNode readJson = mapper.createObjectNode();
            readJson.put("type", "read_receipt");
            readJson.put("readerId", senderId);
            otherSession.sendMessage(new TextMessage(mapper.writeValueAsString(readJson)));
        }
    }

    private void sendNotification(String userId, String message) throws Exception {
        WebSocketSession session = clients.get(userId);
        if (session != null && session.isOpen()) {
            ObjectNode notifJson = mapper.createObjectNode();
            notifJson.put("type", "notification");
            notifJson.put("message", message);
            session.sendMessage(new TextMessage(mapper.writeValueAsString(notifJson)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = sessionUserMap.remove(session.getId());
        if (userId != null) {
            clients.remove(userId);
            log.info("WebSocket disconnected: {}", userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        String userId = sessionUserMap.remove(session.getId());
        if (userId != null) {
            clients.remove(userId);
        }
        log.error("WebSocket error for session {}: {}", session.getId(), exception.getMessage());
    }
}
