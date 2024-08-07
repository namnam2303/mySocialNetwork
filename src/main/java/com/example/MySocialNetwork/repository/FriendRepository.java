package com.example.MySocialNetwork.repository;

import com.example.MySocialNetwork.entity.Friend;
import com.example.MySocialNetwork.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends CrudRepository<Friend, Long> {
    boolean existsByUserAndFriend(User user, User friend);
    Optional<Friend> findByUserAndFriend(User user, User friend);
    List<Friend> findAllByUserAndStatus(User user, Friend.Status status);
}
