package com.kampuskart.repository;

import com.kampuskart.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Long> {
    List<Payout> findAllByOrderByCreatedAtDesc();
    List<Payout> findByUserIdOrderByCreatedAtDesc(Long userId);
}
