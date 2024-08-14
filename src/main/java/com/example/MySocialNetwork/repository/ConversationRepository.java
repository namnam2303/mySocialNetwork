package com.example.MySocialNetwork.repository;

import com.example.MySocialNetwork.entity.Conversation;
import com.example.MySocialNetwork.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends CrudRepository<Conversation, Long> {
    Optional<Conversation> findByPublicId(String publicId);

    List<Conversation> findAllByParticipantsContaining(User user);

    @Query("SELECT c FROM Conversation c WHERE :user MEMBER OF c.participants ORDER BY c.updatedAt DESC")
    List<Conversation> findAllByParticipantOrderByUpdatedAtDesc(User user);

    @Query("SELECT c FROM Conversation c WHERE c.type = 'PRIVATE' AND :user1 MEMBER OF c.participants AND :user2 MEMBER OF c.participants")
    Optional<Conversation> findPrivateConversationBetweenUsers(User user1, User user2);
}