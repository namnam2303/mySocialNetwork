package com.example.MySocialNetwork.controller;

import com.example.MySocialNetwork.entity.Friend;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.exception.User.UserNotFoundException;
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
@RequestMapping("/api/friend")
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
    public ResponseEntity<?> sendFriendRequest(@PathVariable String username, @PathVariable String friendUsername) {
        User user = validateUser(username);
        User friend = validateUser(friendUsername);

        Friend friendRequest = friendService.sendFriendRequest(user, friend);
        return ResponseEntity.ok(friendRequest);
    }

    @PutMapping("/accept/{friendUsername}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable String friendUsername, @Valid @RequestBody User user, BindingResult bindingResult) {
        ResponseEntity<?> errorsMap = mapValidationErrorService.mapValidationError(bindingResult);
        if (errorsMap != null) {
            return errorsMap;
        }
        User friend = validateUser(friendUsername);
        Friend updatedFriend = friendService.acceptFriendRequest(user, friend);
        return ResponseEntity.ok(updatedFriend);
    }

    @PutMapping("/reject/{friendUsername}")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable String friendUsername, @Valid @RequestBody User user, BindingResult bindingResult) {
        ResponseEntity<?> errorsMap = mapValidationErrorService.mapValidationError(bindingResult);
        if (errorsMap != null) {
            return errorsMap;
        }
        User friend = validateUser(friendUsername);
        Friend updatedFriend = friendService.rejectFriendRequest(user, friend);
        return ResponseEntity.ok(updatedFriend);
    }

    @DeleteMapping("/{username}/{friendUsername}")
    public ResponseEntity<Void> unfriend(@PathVariable String username, @PathVariable String friendUsername) {
        User user = validateUser(username);
        User friend = validateUser(friendUsername);

        friendService.unfriend(user, friend);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friends-list/{username}")
    public ResponseEntity<List<User>> getFriendsList(@PathVariable String username) {
        User user = validateUser(username);
        List<User> friendsList = friendService.getFriendsList(user);
        return ResponseEntity.ok(friendsList);
    }



    private User validateUser(String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User with username " + username + " not found");
        }
        return user;
    }
}
