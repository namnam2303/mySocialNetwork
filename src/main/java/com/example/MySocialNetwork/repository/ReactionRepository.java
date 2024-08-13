package com.example.MySocialNetwork.repository;

import com.example.MySocialNetwork.dto.ReactionDTO;
import com.example.MySocialNetwork.entity.Reaction;
import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends CrudRepository<Reaction, Long> {
    Optional<Reaction> findByPublicId(String publicId);
    List<Reaction> findAllByPost(Post post);
    Reaction findByPostAndUser(Post post, User user);

    @Query("SELECT new com.example.MySocialNetwork.dto.ReactionDTO(r.publicId, r.user.username, r.reactionType) FROM Reaction r WHERE r.post.publicId = :postId")
    List<ReactionDTO> findReactionDTOsByPostId(@Param("postId") String postId);
}
