package com.example.MySocialNetwork.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDTO {
    @NotBlank(message = "Username is mandatory")
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Please enter your full name")
    private String fullName;

    @NotNull(message = "Birth date is mandatory")
    private LocalDate birthDate;

    private String avatar;
    private String occupation;
    private String location;
}