package com.example.MySocialNetwork.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WebSocketMessage {

    private String sender;
    private String recipient;
    private String action;
    private String details;
}
