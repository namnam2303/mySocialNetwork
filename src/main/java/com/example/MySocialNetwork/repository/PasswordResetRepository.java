package com.example.MySocialNetwork.repository;

import com.example.MySocialNetwork.entity.PasswordReset;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends CrudRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByToken(String token);
}