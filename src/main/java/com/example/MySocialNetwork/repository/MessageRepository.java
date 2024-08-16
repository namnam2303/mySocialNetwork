package com.example.MySocialNetwork.repository;

import com.example.MySocialNetwork.entity.Conversation;
import com.example.MySocialNetwork.entity.Message;
import com.example.MySocialNetwork.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends CrudRepository<Message, Long> {
    Optional<Message> findByPublicId(String publicId);

    Page<Message> findByConversation(Conversation conversation, Pageable pageable);

    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);

    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.createdAt > :timestamp ORDER BY m.createdAt ASC")
    List<Message> findNewMessagesInConversation(@Param("conversation") Conversation conversation, @Param("timestamp") LocalDateTime timestamp);

    List<Message> findBySenderAndCreatedAtBetween(User sender, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation AND :user NOT MEMBER OF m.readByUsers")
    long countUnreadMessagesForUser(@Param("conversation") Conversation conversation, @Param("user") User user);

    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND :user NOT MEMBER OF m.readByUsers ORDER BY m.createdAt ASC")
    List<Message> findUnreadMessagesForUser(@Param("conversation") Conversation conversation, @Param("user") User user);

}