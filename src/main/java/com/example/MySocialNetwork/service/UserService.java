package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.dto.UserUpdateDTO;
import com.example.MySocialNetwork.exception.Email.EmailAlreadyExistsException;
import com.example.MySocialNetwork.exception.User.UserAlreadyExistException;
import com.example.MySocialNetwork.exception.User.UserNotFoundException;
import com.example.MySocialNetwork.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public User getUserById(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
    }

    public void updateUser(String username, UserUpdateDTO user) {
        User oldUser = getUserByUsername(username);

        // Check for email conflict
        User userWithEmail = userRepository.findByEmail(user.getEmail());
        if (userWithEmail != null && !oldUser.getId().equals(userWithEmail.getId())) {
            throw new RuntimeException("Email already exists");
        }

        // Check for username conflict
        Optional<User> userWithUsername = userRepository.findByUsername(user.getUsername());
        if (userWithUsername.isPresent() && !userWithUsername.get().equals(oldUser)) {
            throw new RuntimeException("Username already exists");
        }

        oldUser.setUsername(user.getUsername().toLowerCase());
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
        user.setUsername(user.getUsername().toLowerCase());
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
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistException("Username already exists");
        }
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Username not found"));
    }

    public User updateAvatar(String userId, MultipartFile avatar) {
        User user = getUserById(userId);
        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = saveAvatar(avatar, userId);
            user.setAvatar(avatarUrl);
            return userRepository.save(user);
        }
        throw new RuntimeException("Avatar file is empty or null");
    }

    private String saveAvatar(MultipartFile avatar, String userPublicId) {
        String baseDir = "statics/user/avatar";
        String userDir = baseDir + "/" + userPublicId;
        File directory = new File(userDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String extension = getFileExtension(avatar.getOriginalFilename());
        String fileName = "avatar_" + timestamp + (extension.isEmpty() ? "" : "." + extension);
        String filePath = userDir + "/" + fileName;
        try {
            Files.copy(avatar.getInputStream(), Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to store avatar", e);
        }
        return filePath;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void setOnline(String username){
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("Username not found " + username + " to set online");
        }
        user.setOnline(true);
    }

    public void setOffline(String username){
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("Username not found " + username + " to set offline");

        }
        user.setOnline(false);
    }
}
