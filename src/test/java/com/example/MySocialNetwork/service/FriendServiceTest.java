package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.Friend;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.repository.FriendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private FriendService friendService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
    }

    @Test
    void sendFriendRequest_Success() {
        when(friendRepository.existsByUserAndFriend(user1, user2)).thenReturn(false);
        when(friendRepository.save(any(Friend.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Friend result = friendService.sendFriendRequest(user1, user2);

        assertNotNull(result);
        assertEquals(user1, result.getUser());
        assertEquals(user2, result.getFriend());
        assertEquals(Friend.Status.PENDING, result.getStatus());
        verify(friendRepository).save(any(Friend.class));
    }

    @Test
    void sendFriendRequest_AlreadyExists() {
        when(friendRepository.existsByUserAndFriend(user1, user2)).thenReturn(true);

        assertThrows(ResourceNotFoundException.class, () -> friendService.sendFriendRequest(user1, user2));
        verify(friendRepository, never()).save(any(Friend.class));
    }

    @Test
    void acceptFriendRequest_Success() {
        Friend pendingRequest = new Friend();
        pendingRequest.setUser(user2);
        pendingRequest.setFriend(user1);
        pendingRequest.setStatus(Friend.Status.PENDING);

        when(friendRepository.findByUserAndFriend(user2, user1)).thenReturn(Optional.of(pendingRequest));
        when(friendRepository.save(any(Friend.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Friend result = friendService.acceptFriendRequest(user1, user2);

        assertNotNull(result);
        assertEquals(Friend.Status.ACCEPTED, result.getStatus());
        verify(friendRepository).save(pendingRequest);
    }

    @Test
    void acceptFriendRequest_NotFound() {
        when(friendRepository.findByUserAndFriend(user2, user1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> friendService.acceptFriendRequest(user1, user2));
        verify(friendRepository, never()).save(any(Friend.class));
    }

    @Test
    void rejectFriendRequest_Success() {
        Friend pendingRequest = new Friend();
        pendingRequest.setUser(user2);
        pendingRequest.setFriend(user1);
        pendingRequest.setStatus(Friend.Status.PENDING);

        when(friendRepository.findByUserAndFriend(user2, user1)).thenReturn(Optional.of(pendingRequest));
        when(friendRepository.save(any(Friend.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Friend result = friendService.rejectFriendRequest(user1, user2);

        assertNotNull(result);
        assertEquals(Friend.Status.REJECTED, result.getStatus());
        verify(friendRepository).save(pendingRequest);
    }

    @Test
    void unfriend_Success() {
        Friend friendship = new Friend();
        friendship.setUser(user1);
        friendship.setFriend(user2);
        friendship.setStatus(Friend.Status.ACCEPTED);

        when(friendRepository.findByUserAndFriend(user1, user2)).thenReturn(Optional.of(friendship));

        friendService.unfriend(user1, user2);

        verify(friendRepository).delete(friendship);
    }

    @Test
    void unfriend_NotFound() {
        when(friendRepository.findByUserAndFriend(user1, user2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> friendService.unfriend(user1, user2));
        verify(friendRepository, never()).delete(any(Friend.class));
    }

    @Test
    void getAllFriends_Success() {
        Friend friend1 = new Friend();
        friend1.setUser(user1);
        friend1.setFriend(user2);
        friend1.setStatus(Friend.Status.ACCEPTED);

        when(friendRepository.findAllByUserAndStatus(user1, Friend.Status.ACCEPTED)).thenReturn(Arrays.asList(friend1));

        List<Friend> result = friendService.getAllFriends(user1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user2, result.get(0).getFriend());
    }

    @Test
    void getFriendsList_Success() {
        Friend friend1 = new Friend();
        friend1.setUser(user1);
        friend1.setFriend(user2);
        friend1.setStatus(Friend.Status.ACCEPTED);

        when(friendRepository.findAllByUserAndStatus(user1, Friend.Status.ACCEPTED)).thenReturn(Arrays.asList(friend1));

        List<User> result = friendService.getFriendsList(user1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user2, result.get(0));
    }

    @Test
    void rejectFriendRequest_Unsuccessful() {
        when(friendRepository.findByUserAndFriend(user2, user1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> friendService.rejectFriendRequest(user1, user2));
        verify(friendRepository, never()).save(any(Friend.class));
    }

    @Test
    void getAllFriends_NotFound() {
        when(friendRepository.findAllByUserAndStatus(user1, Friend.Status.ACCEPTED)).thenReturn(Collections.emptyList());

        List<Friend> result = friendService.getAllFriends(user1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(friendRepository).findAllByUserAndStatus(user1, Friend.Status.ACCEPTED);
    }

    @Test
    void getFriendsList_NotFound() {
        when(friendRepository.findAllByUserAndStatus(user1, Friend.Status.ACCEPTED)).thenReturn(Collections.emptyList());

        List<User> result = friendService.getFriendsList(user1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(friendRepository).findAllByUserAndStatus(user1, Friend.Status.ACCEPTED);
    }
}