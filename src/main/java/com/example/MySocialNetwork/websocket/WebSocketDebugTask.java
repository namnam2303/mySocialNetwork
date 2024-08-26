package com.example.MySocialNetwork.websocket;

import com.example.MySocialNetwork.websocket.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WebSocketDebugTask {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketDebugTask.class);

    @Autowired
    private WebSocketService webSocketService;

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void logActiveConnections() {
        int activeConnections = webSocketService.getActiveSessionCount();
        logger.info("Active WebSocket connections: {}", activeConnections);

        // Optionally, you can log more details about each connection
        webSocketService.getSessions().forEach((id, session) -> {
            logger.debug("Session ID: {}, Remote Address: {}", id, session.getRemoteAddress());
        });
    }
}