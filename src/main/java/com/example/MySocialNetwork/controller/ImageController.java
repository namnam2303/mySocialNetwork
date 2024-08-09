package com.example.MySocialNetwork.controller;

import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.UserNotFoundException;
import com.example.MySocialNetwork.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/post/image")
public class ImageController {
    private final UserService userService;

    @Autowired
    public ImageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{userPublicId}/{fileName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@PathVariable String userPublicId, @PathVariable String fileName) throws IOException {
        User user = userService.findByPublicId(userPublicId);
        if (user == null) {
            throw new UserNotFoundException("User with id " + userPublicId + " not found");
        }
        Path filePath = Paths.get("statics/post/image/" + userPublicId).resolve(fileName).normalize();
        if (Files.exists(filePath) && Files.isReadable(filePath)) {
            return Files.readAllBytes(filePath);
        } else {
            throw new IOException("Could not read file: " + filePath);
        }
    }
}

