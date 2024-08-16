package com.example.MySocialNetwork.repository;

import com.example.MySocialNetwork.entity.Conversation;
import com.example.MySocialNetwork.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConversationRepository extends CrudRepository<Conversation, Long> {
    Optional<Conversation> findByPublicId(String publicId);

    Page<Conversation> findAllByParticipantsContaining(User user, Pageable pageable);

    @Query("SELECT c FROM Conversation c WHERE c.type = 'PRIVATE' AND :user1 MEMBER OF c.participants AND :user2 MEMBER OF c.participants")
    Optional<Conversation> findPrivateConversationBetweenUsers(User user1, User user2);
}