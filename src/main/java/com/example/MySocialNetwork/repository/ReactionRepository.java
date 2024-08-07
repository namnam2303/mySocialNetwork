package com.example.MySocialNetwork.repository;

import com.example.MySocialNetwork.entity.Reaction;
import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends CrudRepository<Reaction, Long> {
    Optional<Reaction> findByPublicId(String publicId);
    List<Reaction> findAllByPost(Post post);
    Reaction findByPostAndUser(Post post, User user);
}
