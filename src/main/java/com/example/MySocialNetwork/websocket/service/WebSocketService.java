package com.example.MySocialNetwork.websocket.service;

import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class WebSocketService {
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

    public void addSession(String sessionId, String username) {
        userSessionMap.put(username, sessionId);
    }



    public String getSessionIdByUsername(String username) {
        return userSessionMap.get(username);
    }
    public void removeSession(String username) {
        userSessionMap.remove(username);
    }


    public Map<String, String> getUserSessionMap() {
        return userSessionMap;
    }


    public void logAllConnectedClients() {
        userSessionMap.forEach((username, sessionId) ->
                System.out.println("User: " + username + ", Session ID: " + sessionId));
    }
}