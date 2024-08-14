package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.Conversation;
import com.example.MySocialNetwork.entity.Message;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.repository.ConversationRepository;
import com.example.MySocialNetwork.repository.MessageRepository;
import com.example.MySocialNetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public Message sendMessage(String conversationPublicId, String senderUsername, String content) {
        Conversation conversation = conversationRepository.findByPublicId(conversationPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with publicId: " + conversationPublicId));

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + senderUsername));

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        message.getReadByUsers().add(sender.getId());

        message = messageRepository.save(message);

        // Update the conversation's last activity
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return message;
    }

    public Message getMessageByPublicId(String publicId) {
        return messageRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with publicId: " + publicId));
    }

    public List<Message> getConversationMessages(String conversationPublicId) {
        Conversation conversation = conversationRepository.findByPublicId(conversationPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with publicId: " + conversationPublicId));

        return messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
    }

    public List<Message> getNewMessagesInConversation(String conversationPublicId, LocalDateTime timestamp) {
        Conversation conversation = conversationRepository.findByPublicId(conversationPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with publicId: " + conversationPublicId));

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
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with publicId: " + conversationPublicId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return messageRepository.countUnreadMessagesForUser(conversation, user);
    }

    public List<Message> getUnreadMessages(String conversationPublicId, String username) {
        Conversation conversation = conversationRepository.findByPublicId(conversationPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with publicId: " + conversationPublicId));
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