package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.Comment;
import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment updateComment(String publicId, Comment commentDetails) {
        Comment existingComment = commentRepository.findByPublicIdAndIsDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with publicId: " + publicId));

        existingComment.setContent(commentDetails.getContent());
        return commentRepository.save(existingComment);
    }

    public void deleteComment(String publicId) {
        Comment comment = commentRepository.findByPublicIdAndIsDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with publicId: " + publicId));

        comment.setIsDeleted(true);
        commentRepository.save(comment);
    }

    public Comment getComment(String publicId) {
        return commentRepository.findByPublicIdAndIsDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with publicId: " + publicId));
    }

    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findAllByPostAndIsDeletedFalse(post);
    }
}
