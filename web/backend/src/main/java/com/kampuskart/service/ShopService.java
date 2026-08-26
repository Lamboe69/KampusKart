package com.kampuskart.service;

import com.kampuskart.dto.ShopDto;
import com.kampuskart.dto.ProductDto;
import com.kampuskart.entity.User;
import com.kampuskart.repository.OrderRepository;
import com.kampuskart.repository.ProductRepository;
import com.kampuskart.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopService {
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;

    public ShopService(UserRepository userRepo, ProductRepository productRepo, OrderRepository orderRepo) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
    }

    public List<ShopDto> list(String campus, String type, String search) {
        List<User> sellers = new ArrayList<>();
        if (type == null || "seller".equals(type)) {
            if (campus != null && !"all".equals(campus)) {
                sellers.addAll(userRepo.findByRoleAndIsActiveTrueAndCampus("seller", campus));
            } else {
                sellers.addAll(userRepo.findByRoleAndIsActiveTrue("seller"));
            }
        }
        if (type == null || "shop".equals(type)) {
            if (campus != null && !"all".equals(campus)) {
                sellers.addAll(userRepo.findByRoleAndIsActiveTrueAndCampus("shop", campus));
            } else {
                sellers.addAll(userRepo.findByRoleAndIsActiveTrue("shop"));
            }
        }

        if (search != null && !search.isEmpty()) {
            String s = search.toLowerCase();
            sellers = sellers.stream()
                .filter(u -> u.getName().toLowerCase().contains(s) ||
                            (u.getDescription() != null && u.getDescription().toLowerCase().contains(s)))
                .collect(Collectors.toList());
        }

        return sellers.stream().map(u -> {
            long productCount = productRepo.countBySellerId(u.getId());
            long salesCount = orderRepo.countCompletedBySellerId(u.getId());
            return ShopDto.from(u, (int) productCount, (int) salesCount);
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getById(String id) {
        User seller = userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Shop not found"));
        long productCount = productRepo.countBySellerId(id);
        long salesCount = orderRepo.countCompletedBySellerId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("shop", ShopDto.from(seller, (int) productCount, (int) salesCount));
        result.put("products", productRepo.findBySellerId(id)
            .stream().map(ProductDto::from).collect(Collectors.toList()));
        return result;
    }
}
