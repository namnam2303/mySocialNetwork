package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.PasswordReset;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.repository.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    public void createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setUser(user);
        passwordReset.setToken(token);
        passwordReset.setExpiresAt(LocalDateTime.now().plusHours(24)); // Token hết hạn sau 24 giờ

        passwordResetRepository.save(passwordReset);

        sendPasswordResetEmail(user.getEmail(), token);
    }

    private void sendPasswordResetEmail(String recipientEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n"
                + "http://localhost:3000/reset-password" + "\n"
                + "and enter your token to update your password\n"
                + "token :" + token);

        mailSender.send(message);
        System.out.println("Sent email and token :" + token);
    }

    public boolean validatePasswordResetToken(String token) {
        PasswordReset passwordReset = passwordResetRepository.findByToken(token)
                .orElse(null);
        if (passwordReset == null) {return false;}

        if (LocalDateTime.now().isAfter(passwordReset.getExpiresAt())) {
            passwordResetRepository.delete(passwordReset);
            return false;
        }

        return true;
    }

    public void resetPassword(String token, String newPassword) {
        PasswordReset passwordReset = passwordResetRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        User user = passwordReset.getUser();
        userService.updatePassword(user, newPassword);

        passwordResetRepository.delete(passwordReset);
    }
}