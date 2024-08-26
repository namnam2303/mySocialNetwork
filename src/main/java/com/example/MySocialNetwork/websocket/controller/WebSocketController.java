package com.example.MySocialNetwork.websocket.controller;

import com.example.MySocialNetwork.websocket.model.WebSocketMessage;
import com.example.MySocialNetwork.websocket.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketService webSocketService;
    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate, WebSocketService webSocketService) {
        this.messagingTemplate = messagingTemplate;
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public WebSocketMessage sendMessage(@Payload WebSocketMessage webSocketMessage) {
        return webSocketMessage;
    }

    @MessageMapping("/addUser")
    @SendTo("/topic/public")
    public WebSocketMessage addUser(@Payload WebSocketMessage webSocketMessage,
                                    SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        String username = webSocketMessage.getSender();
        headerAccessor.getSessionAttributes().put("username", username);
        String sessionId = headerAccessor.getSessionId();
        webSocketService.addSession(sessionId, username);
        return webSocketMessage;
    }

    // Example of sending a message to a specific user
    public void sendPrivateMessage(String username, WebSocketMessage message) {
        messagingTemplate.convertAndSendToUser(
                username,
                "/topic/private",
                message
        );
    }

    // Example of broadcasting a message to all connected clients
    public void broadcastMessage(WebSocketMessage message) {
        messagingTemplate.convertAndSend("/topic/public", message);
    }

    // Add more message handling methods as needed for your application
    @MessageMapping("/newPost")
    @SendTo("/topic/newPost")
    public Long handleNewPost(@Payload Long postId) {
        // You might want to do some processing here
        return postId;
    }

    @MessageMapping("/newComment")
    @SendTo("/topic/newComment")
    public Long handleNewComment(@Payload Long commentId) {
        // You might want to do some processing here
        return commentId;
    }

    @MessageMapping("/newFriendRequest")
    public void handleNewFriendRequest(@Payload WebSocketMessage message) {
        // Process the friend request and send a notification to the recipient
        String recipientUsername = message.getRecipient();
        messagingTemplate.convertAndSendToUser(
                recipientUsername,
                "/topic/friendRequest",
                message
        );
    }

    @MessageMapping("/debug")
    public void handleDebugMessage(WebSocketMessage message) {
        System.out.println("Debug message received: " + message.getAction() + " - " + message.getDetails());
        // Có thể log ra file hoặc gửi đến một hệ thống monitoring
    }

    // Add a method to handle disconnections
    public void handleDisconnect(SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null && webSocketService.getSessionIdByUsername(username) != null){
            webSocketService.removeSession(username);
            System.out.println(headerAccessor.getSessionAttributes().get("username") + " disconnected");
        }
    }

}