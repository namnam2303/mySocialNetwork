package com.example.MySocialNetwork.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email is not valid",min = 5)
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank
    @Size(min = 3, max = 20)
    private String password;

    @NotBlank(message = "Please enter your full name")
    private String fullName;

    @NotNull(message = "Birth date is mandatory")
    private LocalDate birthDate;

}
