package com.example.MySocialNetwork.auth;

import com.example.MySocialNetwork.security.JwtResponse;
import com.example.MySocialNetwork.auth.LoginRequest;
import com.example.MySocialNetwork.auth.RegisterRequest;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.security.config.JwtTokenProvider;
import com.example.MySocialNetwork.service.MapValidationErrorService;
import com.example.MySocialNetwork.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt, authentication.getName()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) {
        ResponseEntity<?> errorResponse = validateRegisterRequest(registerRequest, bindingResult);
        if (errorResponse != null) {
            return errorResponse;
        }

        User user = createUserFromRequest(registerRequest);
        User result = userService.save(user);

        return ResponseEntity.ok(result);
    }

    private ResponseEntity<?> validateRegisterRequest(RegisterRequest registerRequest, BindingResult bindingResult) {
        ResponseEntity<?> errorsMap = mapValidationErrorService.mapValidationError(bindingResult);
        if (errorsMap != null) {
            return errorsMap;
        }

        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email Address already in use!");
        }

        return null;
    }

    private User createUserFromRequest(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("USER");
        user.setFullName(registerRequest.getFullName());
        user.setBirthDate(registerRequest.getBirthDate());
        user.setAvatar(registerRequest.getAvatar());
        user.setOccupation(registerRequest.getOccupation());
        user.setLocation(registerRequest.getLocation());
        return user;
    }
}
