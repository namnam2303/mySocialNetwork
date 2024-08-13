package com.example.MySocialNetwork.dto;

import com.example.MySocialNetwork.entity.Reaction;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReactionDTO {
    private String publicId;
    private String username;
    private Reaction.ReactionType reactionType;

    public ReactionDTO(String publicId, String username, Reaction.ReactionType reactionType) {
        this.publicId = publicId;
        this.username = username;
        this.reactionType = reactionType;
    }
}