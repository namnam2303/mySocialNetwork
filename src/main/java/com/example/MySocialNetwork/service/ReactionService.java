package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.Reaction;
import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;

    @Autowired
    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    public Reaction createReaction(Reaction reaction) {
        return reactionRepository.save(reaction);
    }

    public Reaction updateReaction(Reaction reaction) {
        return reactionRepository.save(reaction);
    }

    public void deleteReaction(String publicId) {
        Reaction reaction = reactionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found with publicId: " + publicId));

        reactionRepository.delete(reaction);
    }

    public Reaction getReaction(String publicId) {
        return reactionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found with publicId: " + publicId));
    }

    public Reaction findByPostAndUser(Post post, User user) {
        return reactionRepository.findByPostAndUser(post, user);
    }

    public List<Reaction> getReactionsByPost(Post post) {
        return reactionRepository.findAllByPost(post);
    }
}
