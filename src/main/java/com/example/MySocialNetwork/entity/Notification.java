package com.example.MySocialNetwork.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Content is mandatory")
    private String content;

    @NotNull(message = "Type is mandatory")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private boolean isRead = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(unique = true)
    private String publicId;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        this.publicId = UUID.randomUUID().toString();
    }

    public enum NotificationType {
        MESSAGE, FRIEND_REQUEST, COMMENT, REACTION, GROUP_INVITE
    }
}
