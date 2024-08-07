package com.example.MySocialNetwork.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Username is mandatory")
    private String username;

    @Column(unique = true)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password should have at least 6 characters")
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private String role = "user";

    @Column(unique = true)
    private String publicId;

    private String avatar;
    @NotBlank(message = "Please enter your full name")
    private String fullName;
    @NotNull(message = "Birth date is mandatory")
    private LocalDate birthDate;
    private String occupation;
    private String location;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Reaction> reactions;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Friend> friends;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<PasswordReset> passwordResets;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Report> reports;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        this.publicId = UUID.randomUUID().toString();
    }
    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}