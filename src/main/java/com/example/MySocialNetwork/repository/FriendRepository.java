package com.example.MySocialNetwork.repository;

import com.example.MySocialNetwork.entity.Friend;
import com.example.MySocialNetwork.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FriendRepository extends CrudRepository<Friend, Long> {
    boolean existsByUserAndFriend(User user, User friend);
    Optional<Friend> findByUserAndFriend(User user, User friend);
    List<Friend> findAllByUserAndStatus(User user, Friend.Status status);
    int countByUserAndStatusAndCreatedAtAfter(User user, Friend.Status status, LocalDateTime createdAt);
    @Query("SELECT f.friend FROM Friend f WHERE f.user = ?1 AND f.status = 'ACCEPTED'")
    List<User> findAllAcceptedFriendsByUser(User user);
    @Query("SELECT f.friend from  Friend  f where f.user =?1 and f.status = 'ACCEPTED' and f.user.isOnline = true ")
    List<User> findAllFriendOnline(User user);
}
