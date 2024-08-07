package com.example.MySocialNetwork.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate reportDate;

    @Column(nullable = false)
    private int numPosts;

    @Column(nullable = false)
    private int numComments;

    @Column(nullable = false)
    private int numFriends;

    @Column(nullable = false)
    private int numLikes;

    @Column(nullable = false)
    private int numLoves;

    @Column(nullable = false)
    private int numAngries;

    @Column(nullable = false)
    private int numSads;

    @Column(nullable = false)
    private int numHahas;

    @Column( updatable = false)
    private LocalDateTime createdAt;
    @Column(updatable = false, unique = true)
    private String publicId;
    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        publicId = UUID.randomUUID().toString();
    }
}