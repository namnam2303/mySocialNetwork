package com.example.MySocialNetwork.controller;

import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.service.PasswordResetService;
import com.example.MySocialNetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserService userService;

    @PostMapping("/request")
    public ResponseEntity<?> requestPasswordReset(@RequestParam("email") String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User with email :" + email + " not found");
        }
        passwordResetService.createPasswordResetTokenForUser(user);
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @RequestParam("password") String newPassword) {
        boolean isTokenValid = passwordResetService.validatePasswordResetToken(token);
        if (!isTokenValid) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password has been reset successfully");
    }
}