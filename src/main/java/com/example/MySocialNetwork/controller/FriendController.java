package com.example.MySocialNetwork.controller;

import com.example.MySocialNetwork.entity.Friend;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.service.FriendService;
import com.example.MySocialNetwork.service.MapValidationErrorService;
import com.example.MySocialNetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;
    private final MapValidationErrorService mapValidationErrorService;

    @Autowired
    public FriendController(FriendService friendService, UserService userService, MapValidationErrorService mapValidationErrorService) {
        this.friendService = friendService;
        this.userService = userService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("/{userPublicId}/{friendPublicId}")
    public ResponseEntity<?> sendFriendRequest(@PathVariable String userPublicId, @PathVariable String friendPublicId) {
        User user = validateUser(userPublicId);
        User friend = validateUser(friendPublicId);

        Friend friendRequest = friendService.sendFriendRequest(user, friend);
        return ResponseEntity.ok(friendRequest);
    }

    @PutMapping("/accept/{friendPublicId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable String friendPublicId, @Valid @RequestBody User user, BindingResult bindingResult) {
        ResponseEntity<?> errorsMap = mapValidationErrorService.mapValidationError(bindingResult);
        if (errorsMap != null) {
            return errorsMap;
        }
        User friend = validateUser(friendPublicId);
        Friend updatedFriend = friendService.acceptFriendRequest(user, friend);
        return ResponseEntity.ok(updatedFriend);
    }

    @PutMapping("/reject/{friendPublicId}")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable String friendPublicId, @Valid @RequestBody User user, BindingResult bindingResult) {
        ResponseEntity<?> errorsMap = mapValidationErrorService.mapValidationError(bindingResult);
        if (errorsMap != null) {
            return errorsMap;
        }
        User friend = validateUser(friendPublicId);
        Friend updatedFriend = friendService.rejectFriendRequest(user, friend);
        return ResponseEntity.ok(updatedFriend);
    }

    @DeleteMapping("/{userPublicId}/{friendPublicId}")
    public ResponseEntity<Void> unfriend(@PathVariable String userPublicId, @PathVariable String friendPublicId) {
        User user = validateUser(userPublicId);
        User friend = validateUser(friendPublicId);

        friendService.unfriend(user, friend);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friends-list/{userPublicId}")
    public ResponseEntity<List<User>> getFriendsList(@PathVariable String userPublicId) {
        User user = validateUser(userPublicId);
        List<User> friendsList = friendService.getFriendsList(user);
        return ResponseEntity.ok(friendsList);
    }



    private User validateUser(String userPublicId) {
        User user = userService.findByPublicId(userPublicId);
        if (user == null) {
            throw new ResourceNotFoundException("User with id " + userPublicId + " not found");
        }
        return user;
    }
}
