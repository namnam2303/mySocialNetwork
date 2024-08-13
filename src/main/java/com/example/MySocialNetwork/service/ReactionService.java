package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.dto.ReactionDTO;
import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.entity.Reaction;
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

    private void createReaction(Reaction reaction) {
        reactionRepository.save(reaction);
    }

    private void updateReaction(Reaction reaction) {
        reactionRepository.save(reaction);
    }

    public void createOrUpdateReaction(Post post, User user, Reaction reaction) {
        Reaction existingReaction = findByPostAndUser(post, user);
        if (existingReaction != null) {
            existingReaction.setReactionType(reaction.getReactionType());
            updateReaction(existingReaction);
        } else {
            reaction.setPost(post);
            reaction.setUser(user);
            createReaction(reaction);
        }
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

    public List<ReactionDTO> findReactionDTOsByPostId(String publicId) {
        return reactionRepository.findReactionDTOsByPostId(publicId);
    }
}
