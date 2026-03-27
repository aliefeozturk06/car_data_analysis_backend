package com.infodif.car_data_analysis.repository;

import com.infodif.car_data_analysis.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.senderUsername = :user1 AND m.receiverUsername = :user2) OR " +
            "(m.senderUsername = :user2 AND m.receiverUsername = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findChatHistory(@Param("user1") String user1, @Param("user2") String user2);

    @Query(value = "SELECT * FROM chat_messages WHERE id IN (" +
            "  SELECT MAX(id) FROM chat_messages " +
            "  WHERE sender_username = :username OR receiver_username = :username " +
            "  GROUP BY CASE " +
            "    WHEN sender_username = :username THEN receiver_username " +
            "    ELSE sender_username " +
            "  END" +
            ") ORDER BY timestamp DESC", nativeQuery = true)
    List<ChatMessage> findRecentChats(@Param("username") String username);
}