package com.example.MySocialNetwork.dto;

import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PostDTO {
    private String publicId;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    private UserDTO user;
    private List<ReactionDTO> reactions;
    private List<CommentDTO> comments;

    public PostDTO(Post post, List<ReactionDTO> reactions) {
        this.publicId = post.getPublicId();
        this.content = post.getContent();
        this.imageUrl = post.getImageUrl();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.isDeleted = post.getIsDeleted();
        this.user = new UserDTO(post.getUser());
        this.reactions = reactions;
        this.comments = post.getComments().stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    @Data
    public static class UserDTO {
        private String username;
        private String fullName;

        public UserDTO(User user) {
            this.username = user.getUsername();
            this.fullName = user.getFullName();
        }
    }

    @Data
    public static class CommentDTO {
        private String publicId;
        private String content;
        private LocalDateTime createdAt;
        private UserDTO user;
        private Boolean isDeleted;

        public CommentDTO(Comment comment) {
            this.publicId = comment.getPublicId();
            this.content = comment.getContent();
            this.createdAt = comment.getCreatedAt();
            this.user = new UserDTO(comment.getUser());
            this.isDeleted = comment.getIsDeleted();
        }
    }
}