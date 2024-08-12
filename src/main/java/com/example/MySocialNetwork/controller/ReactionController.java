package com.example.MySocialNetwork.controller;

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

        Reaction responseReaction = createOrUpdateReaction(post, user, reaction);

        return ResponseEntity.ok(responseReaction);
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

    private Reaction createOrUpdateReaction(Post post, User user, Reaction reaction) {
        Reaction existingReaction = reactionService.findByPostAndUser(post, user);
        if (existingReaction != null) {
            if(existingReaction.getReactionType().equals(reaction.getReactionType())) {
                return existingReaction;
            }
            existingReaction.setReactionType(reaction.getReactionType());
            return reactionService.updateReaction(existingReaction);
        } else {
            reaction.setPost(post);
            reaction.setUser(user);
            return reactionService.createReaction(reaction);
        }
    }
}
