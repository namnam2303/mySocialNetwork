package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.Conversation;
import com.example.MySocialNetwork.entity.Message;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.repository.ConversationRepository;
import com.example.MySocialNetwork.repository.MessageRepository;
import com.example.MySocialNetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public ConversationService(ConversationRepository conversationRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public Conversation createConversation(String name, Conversation.ConversationType type, Set<String> participantUsernames) {
        Set<User> participants = new HashSet<>();
        for (String username : participantUsernames) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
            participants.add(user);
        }

        Conversation conversation = new Conversation();
        conversation.setName(name);
        conversation.setType(type);
        conversation.setParticipants(participants);

        return conversationRepository.save(conversation);
    }

    public Conversation getConversationByPublicId(String publicId) {
        return conversationRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with publicId: " + publicId));
    }

    public Page<Conversation> getUserConversations(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return conversationRepository.findAllByParticipantsContaining(user, pageable);
    }

    public Conversation addParticipantToConversation(String conversationPublicId, String username) {
        Conversation conversation = getConversationByPublicId(conversationPublicId);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        conversation.getParticipants().add(user);
        return conversationRepository.save(conversation);
    }

    public Conversation removeParticipantFromConversation(String conversationPublicId, String username) {
        Conversation conversation = getConversationByPublicId(conversationPublicId);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        conversation.getParticipants().remove(user);
        return conversationRepository.save(conversation);
    }

    public Message addMessageToConversation(String conversationPublicId, String senderUsername, String content) {
        Conversation conversation = getConversationByPublicId(conversationPublicId);
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + senderUsername));

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);

        message = messageRepository.save(message);
        conversation.getMessages().add(message);
        conversationRepository.save(conversation);

        return message;
    }

    public List<Message> getConversationMessages(String conversationPublicId) {
        Conversation conversation = getConversationByPublicId(conversationPublicId);
        return conversation.getMessages();
    }

    public Conversation getOrCreatePrivateConversation(String user1Username, String user2Username) {
        User user1 = userRepository.findByUsername(user1Username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + user1Username));
        User user2 = userRepository.findByUsername(user2Username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + user2Username));

        return conversationRepository.findPrivateConversationBetweenUsers(user1, user2)
                .orElseGet(() -> {
                    Set<String> participants = new HashSet<>();
                    participants.add(user1Username);
                    participants.add(user2Username);
                    String name = user1Username + " and " + user2Username;
                    return createConversation(name, Conversation.ConversationType.PRIVATE, participants);
                });
    }
}