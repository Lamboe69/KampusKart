package com.kampuskart.service;

import com.kampuskart.dto.*;
import com.kampuskart.entity.User;
import com.kampuskart.repository.UserRepository;
import com.kampuskart.security.JwtUtil;
import com.kampuskart.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setName(req.getName());
        user.setRole(req.resolveRole());
        user.setSellerType("shop".equals(user.getRole()) ? (req.getSellerType() != null ? req.getSellerType() : "shop") : req.getSellerType());
        user.setCampus(req.getCampus());
        user.setPhone(req.getPhone());
        user.setImage("https://api.dicebear.com/7.x/avataaars/svg?seed=" + req.getName().replace(" ", "+"));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepo.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, UserDto.from(user));
    }

    public AuthResponse login(String email, String password) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, UserDto.from(user));
    }

    public UserDto getProfile(Authentication auth) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepo.findById(principal.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDto.from(user);
    }

    public UserDto getUserById(String id) {
        User user = userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto dto = new UserDto();
        dto = UserDto.from(user);
        return dto;
    }

    @Transactional
    public UserDto updateProfile(Authentication auth, Map<String, Object> updates) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepo.findById(principal.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (updates.containsKey("name")) user.setName((String) updates.get("name"));
        if (updates.containsKey("phone")) user.setPhone((String) updates.get("phone"));
        if (updates.containsKey("campus")) user.setCampus((String) updates.get("campus"));
        if (updates.containsKey("description")) user.setDescription((String) updates.get("description"));
        if (updates.containsKey("image")) user.setImage((String) updates.get("image"));
        if (updates.containsKey("deliveryZones")) user.setDeliveryZones((String) updates.get("deliveryZones"));
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepo.save(user);
        return UserDto.from(user);
    }

    @Transactional
    public Map<String, String> changePassword(Authentication auth, String currentPassword, String newPassword) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepo.findById(principal.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
        return Map.of("message", "Password changed successfully");
    }

    @Transactional
    public Map<String, String> deleteAccount(Authentication auth, String password) {
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepo.findById(principal.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }
        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
        return Map.of("message", "Account deleted successfully");
    }
}
