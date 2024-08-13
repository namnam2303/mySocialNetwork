package com.example.MySocialNetwork.controller;


import com.example.MySocialNetwork.dto.PostDTO;
import com.example.MySocialNetwork.dto.ReactionDTO;
import com.example.MySocialNetwork.entity.Comment;
import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.service.MapValidationErrorService;
import com.example.MySocialNetwork.service.PostService;
import com.example.MySocialNetwork.service.ReactionService;
import com.example.MySocialNetwork.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final MapValidationErrorService mapValidationErrorService;
    private final ReactionService reactionService;

    @Autowired
    public PostController(PostService postService, UserService userService, MapValidationErrorService mapValidationErrorService, ReactionService reactionService) {
        this.postService = postService;
        this.userService = userService;
        this.mapValidationErrorService = mapValidationErrorService;
        this.reactionService = reactionService;
    }

    @PostMapping("/{username}")
    public ResponseEntity<?> createPost(@PathVariable String username,
                                        @RequestParam("content") String content,
                                        @RequestParam(value = "image", required = false) MultipartFile image) {
        List<Post> newList = postService.createPost(username, content, image);
        return ResponseEntity.ok(newList);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<?> updatePost(@PathVariable String publicId, @Valid @RequestBody Post postDetails, BindingResult bindingResult) {
        ResponseEntity<?> errorsMap = mapValidationErrorService.mapValidationError(bindingResult);
        if (errorsMap != null) {
            return errorsMap;
        }
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
            Post deletedPost = postService.deletePost(publicId);
            return ResponseEntity.ok().body(new PostDTO(deletedPost, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<?> getPost(@PathVariable String publicId) {
        try {
            Post post = postService.getPostById(publicId);
            List<ReactionDTO> reactions = reactionService.findReactionDTOsByPostId(publicId);
            PostDTO postDTO = new PostDTO(post, reactions);
            return ResponseEntity.ok(postDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllPostsByUser(@PathVariable String userId) {
        User user = userService.findByPublicId(userId);
        if (user != null) {
            List<Post> posts = postService.getAllPostsByUser(user);
            List<List<ReactionDTO>> reactionDTOList = posts.stream()
                    .map(post -> reactionService.findReactionDTOsByPostId(post.getPublicId()))
                    .toList();

            List<PostDTO> postDTOList = IntStream.range(0, posts.size())
                    .mapToObj(i -> new PostDTO(posts.get(i), reactionDTOList.get(i)))
                    .toList();

            return ResponseEntity.ok(postDTOList);
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    @GetMapping("/{publicId}/comments")
    public ResponseEntity<?> getCommentsByPost(@PathVariable String publicId) {
        try {
            List<Comment> comments = postService.getCommentsByPost(publicId);
            return ResponseEntity.ok(comments);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}