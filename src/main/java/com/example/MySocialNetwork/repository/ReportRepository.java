package com.example.MySocialNetwork.repository;

import com.example.MySocialNetwork.entity.Report;
import com.example.MySocialNetwork.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends CrudRepository<Report, Long> {
    Optional<Report> findByPublicId(String publicId);
    List<Report> findAllByUser(User user);
}
