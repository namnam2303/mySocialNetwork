package com.example.MySocialNetwork.controller;

import com.example.MySocialNetwork.entity.Comment;
import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.exception.User.UserNotFoundException;
import com.example.MySocialNetwork.service.CommentService;
import com.example.MySocialNetwork.service.MapValidationErrorService;
import com.example.MySocialNetwork.service.PostService;
import com.example.MySocialNetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;
    private final MapValidationErrorService mapValidationErrorService;

    @Autowired
    public CommentController(CommentService commentService, PostService postService, UserService userService, MapValidationErrorService mapValidationErrorService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("/{postPublicId}/{username}")
    public ResponseEntity<?> createComment(@PathVariable String postPublicId, @PathVariable String username, @Valid @RequestBody Comment comment, BindingResult bindingResult) {
        ResponseEntity<?> errorsMap = mapValidationErrorService.mapValidationError(bindingResult);
        if (errorsMap != null) {
            return errorsMap;
        }
        Post post = postService.getPostById(postPublicId);
        User user = userService.getUserByUsername(username);
        if(user == null) {
            throw new UserNotFoundException("User with username " + username + " not found");
        }
        if (post == null) {
            throw new ResourceNotFoundException("Post with id " + postPublicId + " not found");
        }
        comment.setPost(post);
        comment.setUser(user);
        Comment createdComment = commentService.createComment(comment);
        return ResponseEntity.ok(createdComment);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<Comment> updateComment(@PathVariable String publicId, @Valid @RequestBody Comment commentDetails) {
        Comment updatedComment = commentService.updateComment(publicId, commentDetails);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String publicId) {
        commentService.deleteComment(publicId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<Comment> getComment(@PathVariable String publicId) {
        Comment comment = commentService.getComment(publicId);
        return ResponseEntity.ok(comment);
    }

}
