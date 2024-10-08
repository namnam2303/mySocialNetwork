package com.example.MySocialNetwork.controller;

import com.example.MySocialNetwork.dto.PostDTO;
import com.example.MySocialNetwork.dto.ReactionDTO;
import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.entity.Reaction;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.exception.User.UserNotFoundException;
import com.example.MySocialNetwork.service.MapValidationErrorService;
import com.example.MySocialNetwork.service.PostService;
import com.example.MySocialNetwork.service.ReactionService;
import com.example.MySocialNetwork.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reaction")
public class ReactionController {

    private final ReactionService reactionService;
    private final PostService postService;
    private final UserService userService;
    private final MapValidationErrorService mapValidationErrorService;

    @Autowired
    public ReactionController(ReactionService reactionService, PostService postService, UserService userService, MapValidationErrorService mapValidationErrorService) {
        this.reactionService = reactionService;
        this.postService = postService;
        this.userService = userService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("/{postPublicId}/{username}")
    public ResponseEntity<?> createOrUpdateReaction(@PathVariable String postPublicId, @PathVariable String username, @Valid @RequestBody Reaction reaction, BindingResult bindingResult) {
        ResponseEntity<?> errorsMap = mapValidationErrorService.mapValidationError(bindingResult);
        if (errorsMap != null) {
            return errorsMap;
        }
        Post post = validatePost(postPublicId);
        User user = validateUser(username);

        reactionService.createOrUpdateReaction(post, user, reaction);
        Post updatedPost = postService.getPostById(post.getPublicId());
        List<ReactionDTO> newListReaction = reactionService.getReactionsByPost(updatedPost).stream()
                .map(reactionItem -> new ReactionDTO(
                        reactionItem.getPublicId(),
                        reactionItem.getUser().getUsername(),
                        reactionItem.getReactionType()

                ))
                .toList();
        PostDTO newPostDTO = new PostDTO(post, newListReaction);
        return ResponseEntity.ok(newPostDTO);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteReaction(@PathVariable @NotNull String publicId) {
        reactionService.deleteReaction(publicId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<Reaction> getReaction(@PathVariable String publicId) {
        Reaction reaction = reactionService.getReaction(publicId);
        return ResponseEntity.ok(reaction);
    }

    private @NotNull Post validatePost(String postPublicId) {
        Post post = postService.getPostById(postPublicId);
        if (post == null) {
            throw new ResourceNotFoundException("Post with id " + postPublicId + " not found");
        }
        return post;
    }

    private @NotNull User validateUser(String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User with username " + username+ " not found");
        }
        return user;
    }

}
