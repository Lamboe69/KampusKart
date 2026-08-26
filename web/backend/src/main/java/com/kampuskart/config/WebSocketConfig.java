package com.kampuskart.config;

import com.kampuskart.security.JwtUtil;
import com.kampuskart.websocket.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatHandler;
    private final JwtUtil jwtUtil;

    public WebSocketConfig(ChatWebSocketHandler chatHandler, JwtUtil jwtUtil) {
        this.chatHandler = chatHandler;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatHandler, "/ws")
            .setAllowedOrigins("*");
    }
}
