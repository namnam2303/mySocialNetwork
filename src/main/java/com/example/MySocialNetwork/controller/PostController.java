package com.example.MySocialNetwork.controller;


import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.service.MapValidationErrorService;
import com.example.MySocialNetwork.service.PostService;
import com.example.MySocialNetwork.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final MapValidationErrorService mapValidationErrorService;

    @Autowired
    public PostController(PostService postService, UserService userService, MapValidationErrorService mapValidationErrorService) {
        this.postService = postService;
        this.userService = userService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("/{userPublicId}")
    public ResponseEntity<?> createPost(@PathVariable String userPublicId,
                                        @RequestParam("content") String content,
                                        @RequestParam("image") MultipartFile image) {
        Post createdPost = postService.createPost(userPublicId, content, image);
        return ResponseEntity.ok(createdPost);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<?> updatePost(@PathVariable String publicId, @Valid @RequestBody Post postDetails) {
        try {
            Post updatedPost = postService.updatePost(publicId, postDetails);
            return ResponseEntity.ok(updatedPost);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> deletePost(@PathVariable String publicId) {
        try {
            postService.deletePost(publicId);
            return ResponseEntity.ok().body("Post deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<?> getPost(@PathVariable String publicId) {
        try {
            Post post = postService.getPost(publicId);
            return ResponseEntity.ok(post);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllPostsByUser(@PathVariable String userId) {
        User user = userService.findByPublicId(userId);
        if (user != null) {
            List<Post> posts = postService.getAllPostsByUser(user);
            return ResponseEntity.ok(posts);
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }
}