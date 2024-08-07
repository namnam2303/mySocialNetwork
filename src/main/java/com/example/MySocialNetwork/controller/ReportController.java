package com.example.MySocialNetwork.controller;

import com.example.MySocialNetwork.entity.Report;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.service.MapValidationErrorService;
import com.example.MySocialNetwork.service.ReportService;
import com.example.MySocialNetwork.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;
    private final MapValidationErrorService mapValidationErrorService;

    @Autowired
    public ReportController(ReportService reportService, UserService userService, MapValidationErrorService mapValidationErrorService) {
        this.reportService = reportService;
        this.userService = userService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("/{userPublicId}")
    public ResponseEntity<?> createReport(@PathVariable String userPublicId, @Valid @RequestBody Report report, BindingResult bindingResult) {
        ResponseEntity<?> errorsMap = mapValidationErrorService.mapValidationError(bindingResult);
        if (errorsMap != null) {
            return errorsMap;
        }

        User user = userService.findByPublicId(userPublicId);
        if (user == null) {
            throw new ResourceNotFoundException("User with id " + userPublicId + " not found");
        }
        report.setUser(user);
        Report createdReport = reportService.createReport(report);
        return ResponseEntity.ok(createdReport);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<?> updateReport(@PathVariable String publicId, @Valid @RequestBody Report reportDetails, BindingResult bindingResult) {
        ResponseEntity<?> errorsMap = mapValidationErrorService.mapValidationError(bindingResult);
        if (errorsMap != null) {
            return errorsMap;
        }
        try {
            Report updatedReport = reportService.updateReport(publicId, reportDetails);
            return ResponseEntity.ok(updatedReport);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> deleteReport(@PathVariable String publicId) {
        try {
            reportService.deleteReport(publicId);
            return ResponseEntity.ok().body("Report deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<?> getReport(@PathVariable String publicId) {
        try {
            Report report = reportService.getReport(publicId);
            return ResponseEntity.ok(report);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userPublicId}")
    public ResponseEntity<?> getAllReportsByUser(@PathVariable String userPublicId) {
        User user = userService.findByPublicId(userPublicId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        List<Report> reports = reportService.getAllReportsByUser(user);
        return ResponseEntity.ok(reports);
    }
}
