package com.kampuskart.repository;

import com.kampuskart.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.senderId = :userId1 AND m.receiverId = :userId2) OR (m.senderId = :userId2 AND m.receiverId = :userId1) ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("userId1") String userId1, @Param("userId2") String userId2);

    @Query("SELECT m FROM Message m WHERE m.senderId = :userId OR m.receiverId = :userId ORDER BY m.createdAt DESC")
    List<Message> findByUserId(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.senderId = :senderId AND m.receiverId = :receiverId AND m.read = false")
    void markAsRead(@Param("senderId") String senderId, @Param("receiverId") String receiverId);

    @Query("SELECT m FROM Message m WHERE m.id = (SELECT MAX(m2.id) FROM Message m2 WHERE (m2.senderId = :userId1 AND m2.receiverId = :userId2) OR (m2.senderId = :userId2 AND m2.receiverId = :userId1))")
    Message findLastMessage(@Param("userId1") String userId1, @Param("userId2") String userId2);

    long countByReceiverIdAndReadFalse(String receiverId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.senderId = :senderId AND m.receiverId = :receiverId AND m.read = false")
    long countUnreadFromSender(@Param("senderId") String senderId, @Param("receiverId") String receiverId);
}
