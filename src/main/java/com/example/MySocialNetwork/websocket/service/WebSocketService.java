package com.example.MySocialNetwork.websocket.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class WebSocketService {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session.getId());
    }

    public Map<String, WebSocketSession> getSessions() {
        return sessions;
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}