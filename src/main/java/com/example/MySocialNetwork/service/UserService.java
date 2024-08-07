package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.UserNotFoundException;
import com.example.MySocialNetwork.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Getter
@Setter
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(String id) {
        Optional<User> user = userRepository.findByPublicId(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
    }

    public void updateUser(String id, User user) {
        User oldUser = getUserById(id);
        User userWithEmail = userRepository.findByEmail(user.getEmail());
        if (userWithEmail != null && !Objects.equals(userWithEmail.getId(), oldUser.getId())) {
            throw new RuntimeException("Email already exists");
        } else {
            oldUser.setUsername(user.getUsername());
            oldUser.setEmail(user.getEmail());
            oldUser.setFullName(user.getFullName());
            oldUser.setAvatar(user.getAvatar());
            userRepository.save(oldUser);
        }
    }

    public User findByPublicId(String id) {
        Optional<User> user = userRepository.findByPublicId(id);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserNotFoundException("User with id " + id + " not found");
    }

}
