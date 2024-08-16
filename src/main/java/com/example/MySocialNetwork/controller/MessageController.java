package com.example.MySocialNetwork.controller;

import com.example.MySocialNetwork.entity.Conversation;
import com.example.MySocialNetwork.entity.Message;
import com.example.MySocialNetwork.service.ConversationService;
import com.example.MySocialNetwork.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;
    private final ConversationService conversationService;

    @Autowired
    public MessageController(MessageService messageService, ConversationService conversationService) {
        this.messageService = messageService;
        this.conversationService = conversationService;
    }

    @GetMapping("/conversation")
    public ResponseEntity<Page<Message>> getConversationMessages(
            @RequestParam String senderUsername,
            @RequestParam String receiverUsername,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Conversation conversation = conversationService.getOrCreatePrivateConversation(senderUsername, receiverUsername);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Message> messages = messageService.getConversationMessages(conversation, pageRequest);
        return ResponseEntity.ok(messages);
    }


    @GetMapping("/user/{username}")
    public ResponseEntity<Page<Conversation>> getUserConversations(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Conversation> conversations = conversationService.getUserConversations(username, pageRequest);
        return ResponseEntity.ok(conversations);
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @RequestParam String conversationId,
            @RequestParam String senderUsername,
            @RequestParam(required = false) String receiverUsername,
            @RequestParam String content) {
        Message message = messageService.sendMessage(conversationId, senderUsername, receiverUsername, content);
        return ResponseEntity.ok(message);
    }
}