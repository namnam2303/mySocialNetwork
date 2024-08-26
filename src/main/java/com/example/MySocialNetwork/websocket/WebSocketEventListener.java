package com.example.MySocialNetwork.websocket;

import com.example.MySocialNetwork.websocket.controller.WebSocketController;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final WebSocketController webSocketController;

    public WebSocketEventListener(WebSocketController webSocketController) {
        this.webSocketController = webSocketController;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        webSocketController.handleDisconnect(headerAccessor);
    }
}