package com.example.MySocialNetwork.repository;

import com.example.MySocialNetwork.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Long> {

    Optional<User> findById(Long id);

    User findByUsername(String username);
    Optional<User> findByPublicId(String publicId);
    User findByEmail(String email);
}
