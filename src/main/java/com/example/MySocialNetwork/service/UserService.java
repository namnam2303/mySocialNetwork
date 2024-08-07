package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.UserNotFoundException;
import com.example.MySocialNetwork.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Getter
@Setter
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User getUserById(String id) {
        return userRepository.findByPublicId(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    public void updateUser(String id, User user) {
        User oldUser = getUserById(id);

        // Check for email conflict
        User userWithEmail = userRepository.findByEmail(user.getEmail());
        if (userWithEmail != null && !Objects.equals(userWithEmail.getId(), oldUser.getId())) {
            throw new RuntimeException("Email already exists");
        }

        // Check for username conflict
        Optional<User> userWithUsername = userRepository.findByUsername(user.getUsername());
        if (userWithUsername.isPresent() && userWithUsername.get().equals(oldUser)) {
            throw new RuntimeException("Username already exists");
        }

        oldUser.setUsername(user.getUsername());
        oldUser.setEmail(user.getEmail());
        oldUser.setFullName(user.getFullName());
        oldUser.setAvatar(user.getAvatar());
        userRepository.save(oldUser);
    }

    public User findByPublicId(String id) {
        return userRepository.findByPublicId(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        return userRepository.save(user);
    }
}
