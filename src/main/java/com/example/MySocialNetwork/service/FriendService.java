package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.Friend;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendService {

    private final FriendRepository friendRepository;

    @Autowired
    public FriendService(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    public Friend sendFriendRequest(User user, User friend) {
        if (friendRepository.existsByUserAndFriend(user, friend)) {
            throw new ResourceNotFoundException("Friend request already exists");
        }

        Friend friendRequest = new Friend();
        friendRequest.setUser(user);
        friendRequest.setFriend(friend);
        friendRequest.setStatus(Friend.Status.PENDING);
        return friendRepository.save(friendRequest);
    }

    public Friend acceptFriendRequest(User user, User friend) {
        Friend friendRequest = friendRepository.findByUserAndFriend(friend, user)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        friendRequest.setStatus(Friend.Status.ACCEPTED);
        return friendRepository.save(friendRequest);
    }

    public Friend rejectFriendRequest(User user, User friend) {
        Friend friendRequest = friendRepository.findByUserAndFriend(friend, user)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        friendRequest.setStatus(Friend.Status.REJECTED);
        return friendRepository.save(friendRequest);
    }

    public void unfriend(User user, User friend) {
        Friend friendRelationship = friendRepository.findByUserAndFriend(user, friend)
                .orElseThrow(() -> new ResourceNotFoundException("Friend relationship not found"));

        friendRepository.delete(friendRelationship);
    }


    public List<Friend> getAllFriends(User user) {
        return friendRepository.findAllByUserAndStatus(user, Friend.Status.ACCEPTED);
    }

    public List<User> getFriendsList(User user) {
        List<Friend> friends = getAllFriends(user);
        return friends.stream()
                .map(Friend::getFriend)
                .collect(Collectors.toList());
    }
}
