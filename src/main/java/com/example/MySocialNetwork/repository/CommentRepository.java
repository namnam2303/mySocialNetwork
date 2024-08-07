package com.example.MySocialNetwork.repository;

import com.example.MySocialNetwork.entity.Comment;
import com.example.MySocialNetwork.entity.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    Optional<Comment> findByPublicIdAndIsDeletedFalse(String publicId);
    List<Comment> findAllByPostAndIsDeletedFalse(Post post);
}
