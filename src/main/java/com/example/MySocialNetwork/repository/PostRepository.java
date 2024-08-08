package com.example.MySocialNetwork.repository;

import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {
    Optional<Post> findByPublicId(String publicId);
    List<Post> findAllByUserAndIsDeletedFalse(User user);
    Optional<Post> findByPublicIdAndIsDeletedFalse(String publicId);
    int countByUserAndCreatedAtAfter(User user, LocalDateTime createdAt);
    int countLikesByUserAndCreatedAtAfter(User user, LocalDateTime createdAt);
}
