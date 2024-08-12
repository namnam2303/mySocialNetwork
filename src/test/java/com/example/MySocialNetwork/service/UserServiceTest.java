package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.dto.UserUpdateDTO;
import com.example.MySocialNetwork.exception.User.UserNotFoundException;
import com.example.MySocialNetwork.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() {
        // Arrange
        String username = "bbb";
        User expectedUser = new User();
        expectedUser.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.getUserById(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());

        verify(userRepository).findByUsername(username);
    }

    @Test
    void getUserById_NonExistingUser_ThrowsUserNotFoundException() {
        // Arrange
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void updateUser_ValidUpdate_Success() {
        // Arrange
        String username = "testduser";
        User existingUser = new User();
        existingUser.setPublicId("USER1");
        existingUser.setUsername(username);
        existingUser.setEmail("nam@email.com");

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setUsername("newUser1");
        updateDTO.setEmail("new@email.com");
        updateDTO.setFullName("Unit Test new name");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(null);
        when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.empty());

        // Act
        userService.updateUser(username, updateDTO);

        // Assert
        verify(userRepository).save(existingUser);
        assertEquals(updateDTO.getUsername(), existingUser.getUsername());
        assertEquals(updateDTO.getEmail(), existingUser.getEmail());
        assertEquals(updateDTO.getFullName(), existingUser.getFullName());
    }

    @Test
    void updateUser_EmailConflict_ThrowsRuntimeException() {
        // Arrange
        String username = "existinguser";
        User existingUser = new User();
        existingUser.setId(12l);
        existingUser.setUsername(username);

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("conflict@email.com");

        User conflictUser = new User();
        conflictUser.setId(245l);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(conflictUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.updateUser(username, updateDTO));
    }

    @Test
    void updateUser_UsernameConflict_ThrowsRuntimeException(){
        // arrange
        String currentusername = "existinguser";
        User existingUser = new User();
        existingUser.setUsername(currentusername);
        existingUser.setId(54l);

        String newUsername = "conflictusername";

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setUsername(newUsername);

        User conflictUser = new User();
        conflictUser.setId(245l);
        conflictUser.setUsername(newUsername);


        when(userRepository.findByUsername(currentusername)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.of(conflictUser));

        //Act and asset
        assertThrows(RuntimeException.class, () -> userService.updateUser(currentusername, updateDTO));

        // Verify
        verify(userRepository).findByUsername(currentusername);
        verify(userRepository).findByUsername(newUsername);
        verify(userRepository, never()).save(any(User.class));
    }
}