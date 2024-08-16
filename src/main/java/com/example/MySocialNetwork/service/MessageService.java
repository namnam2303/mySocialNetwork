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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, ConversationRepository conversationRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Message sendMessage(String conversationId, String senderUsername, String receiverUsername, String content) {
        User sender = getUserByUsername(senderUsername);
        Conversation conversation = getOrCreateConversation(conversationId, sender, receiverUsername);
        Message message = createAndSaveMessage(conversation, sender, content);
        updateConversationTimestamp(conversation);
        return message;
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    private Conversation getOrCreateConversation(String conversationId, User sender, String receiverUsername) {
        if (conversationId.startsWith("newConversation-")) {
            return createNewConversation(sender, receiverUsername);
        } else {
            return getExistingConversation(conversationId);
        }
    }

    private Conversation createNewConversation(User sender, String receiverUsername) {
        if (receiverUsername == null) {
            throw new IllegalArgumentException("Receiver username is required for new conversations");
        }
        User receiver = getUserByUsername(receiverUsername);

        Set<User> participants = new HashSet<>();
        participants.add(sender);
        participants.add(receiver);

        Conversation conversation = new Conversation();
        conversation.setName(sender.getUsername() + " and " + receiver.getUsername());
        conversation.setType(Conversation.ConversationType.PRIVATE);
        conversation.setParticipants(participants);
        return conversationRepository.save(conversation);
    }

    private Conversation getExistingConversation(String conversationId) {
        return conversationRepository.findByPublicId(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with publicId: " + conversationId));
    }

    private Message createAndSaveMessage(Conversation conversation, User sender, String content) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    private void updateConversationTimestamp(Conversation conversation) {
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    public Message getMessageByPublicId(String publicId) {
        return messageRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with Id: " + publicId));
    }

    public Page<Message> getConversationMessages(Conversation conversation, Pageable pageable) {
        return messageRepository.findByConversation(conversation, pageable);
    }

    public List<Message> getNewMessagesInConversation(String conversationPublicId, LocalDateTime timestamp) {
        Conversation conversation = conversationRepository.findByPublicId(conversationPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with Id: " + conversationPublicId));

        return messageRepository.findNewMessagesInConversation(conversation, timestamp);
    }

    @Transactional
    public void markMessageAsRead(String messagePublicId, String username) {
        Message message = getMessageByPublicId(messagePublicId);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Set<Long> readByUsers = message.getReadByUsers();
        readByUsers.add(user.getId());
        message.setReadByUsers(readByUsers);
        messageRepository.save(message);
    }

    public long getUnreadMessageCount(String conversationPublicId, String username) {
        Conversation conversation = conversationRepository.findByPublicId(conversationPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with Id: " + conversationPublicId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return messageRepository.countUnreadMessagesForUser(conversation, user);
    }

    public List<Message> getUnreadMessages(String conversationPublicId, String username) {
        Conversation conversation = conversationRepository.findByPublicId(conversationPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with Id: " + conversationPublicId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return messageRepository.findUnreadMessagesForUser(conversation, user);
    }

    public List<Message> getUserMessagesBetweenDates(String username, LocalDateTime start, LocalDateTime end) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return messageRepository.findBySenderAndCreatedAtBetween(user, start, end);
    }

    @Transactional
    public Message updateMessage(String messagePublicId, String content) {
        Message message = getMessageByPublicId(messagePublicId);
        message.setContent(content);
        return messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(String messagePublicId) {
        Message message = getMessageByPublicId(messagePublicId);
        messageRepository.delete(message);
    }
}