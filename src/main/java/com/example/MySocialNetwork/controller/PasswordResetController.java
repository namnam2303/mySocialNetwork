package com.example.MySocialNetwork.controller;

import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.service.PasswordResetService;
import com.example.MySocialNetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserService userService;

    @PostMapping("/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found with email: " + email);
        }
        // Xử lý yêu cầu đặt lại mật khẩu
        passwordResetService.createPasswordResetTokenForUser(user);
        return ResponseEntity.ok("Password reset email sent");
    }


    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        String token = requestBody.get("token");
        String newPassword = requestBody.get("password");

        if (token == null || token.isEmpty()) {
            return ResponseEntity.ok().body("Token is required");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.ok().body("New password is required");
        }
        if (newPassword.length() < 3 || newPassword.length() > 20) {
            return ResponseEntity.ok().body("Password must be between 3 and 20 characters");
        }

        boolean isTokenValid = passwordResetService.validatePasswordResetToken(token);
        if (!isTokenValid) {
            return ResponseEntity.ok().body("Invalid or expired token");
        }

        try {
            passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok().body("Password has been reset successfully");
        } catch (Exception e) {
            return ResponseEntity.ok().body("An error occurred while resetting password");
        }
    }

}