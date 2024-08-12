package com.example.MySocialNetwork.controller;

import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.service.PostService;
import com.example.MySocialNetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/timeline")
public class TimelineController {

    private final UserService userService;
    private final PostService postService;

    @Autowired
    public TimelineController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getTimeline(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        List<Post> timelinePosts = postService.getTimelinePosts(user);
        System.out.println("Get timeline from user " + user.getUsername() + " " + timelinePosts.size());
        return ResponseEntity.ok(timelinePosts);
    }
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getPostsOfUser(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        List<Post> postsFromUser = postService.getUserPosts(user);
        System.out.println("Get timeline from user " + user.getUsername() + " " + postsFromUser.size());
        return ResponseEntity.ok(postsFromUser);
    }
}
