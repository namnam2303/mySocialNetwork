package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.Comment;
import com.example.MySocialNetwork.entity.Post;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.repository.CommentRepository;
import com.example.MySocialNetwork.repository.FriendRepository;
import com.example.MySocialNetwork.repository.PostRepository;
import com.example.MySocialNetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final FriendRepository friendRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository, FriendRepository friendRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.friendRepository = friendRepository;
    }

    public Post createPost(String userPublicId, String content, MultipartFile image) {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with publicId: " + userPublicId));

        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        if (image != null && !image.isEmpty()) {
            post.setImageUrl(saveImage(image, userPublicId));
        }
        return postRepository.save(post);
    }

    public Post updatePost(String publicId, Post postDetails) {
        Post existingPost = postRepository.findByPublicIdAndIsDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with publicId: " + publicId));

        existingPost.setContent(postDetails.getContent());
        existingPost.setImageUrl(postDetails.getImageUrl());
        return postRepository.save(existingPost);
    }

    public void deletePost(String publicId) {
        Post post = postRepository.findByPublicIdAndIsDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with publicId: " + publicId));

        postRepository.delete(post);
    }

    public Post getPostById(String publicId) {
        return postRepository.findByPublicIdAndIsDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with publicId: " + publicId));
    }

    public List<Post> getAllPostsByUser(User user) {
        return postRepository.findAllByUserAndIsDeletedFalse(user);
    }

    public List<Post> getTimelinePosts(User user) {
        List<User> friends = friendRepository.findAllAcceptedFriendsByUser(user);
        return postRepository.findTop20ByUserInOrderByCreatedAtDesc(friends);
    }
    private String saveImage(MultipartFile image, String userPublicId) {
        // Đường dẫn cơ sở cho các tệp ảnh
        String baseDir = "statics/post/image";
        // Tạo thư mục người dùng nếu chưa tồn tại
        String userDir = baseDir + "/" + userPublicId;
        File directory = new File(userDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // Tạo tên tệp dựa trên ngày, giờ, tháng, và năm
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String extension = getFileExtension(image.getOriginalFilename());
        String fileName = timestamp + (extension.isEmpty() ? "" : "." + extension);
        String filePath = userDir + "/" + fileName;
        try {
            Files.copy(image.getInputStream(), Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
        return filePath;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public List<Comment> getCommentsByPost(String publicId) {
        Post post = getPostById(publicId);
        return commentRepository.findAllByPostAndIsDeletedFalse(post);
    }
}
