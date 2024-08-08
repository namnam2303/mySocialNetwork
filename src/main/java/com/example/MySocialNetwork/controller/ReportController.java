package com.example.MySocialNetwork.controller;

import com.example.MySocialNetwork.entity.Report;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.service.ReportService;
import com.example.MySocialNetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    @Autowired
    public ReportController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping("/{userPublicId}")
    public ResponseEntity<byte[]> generateReport(@PathVariable String userPublicId) {
        User user = userService.findByPublicId(userPublicId);
        Report report = reportService.generateReport(user);

        try {
            // Lưu báo cáo vào tệp
            String filePath = reportService.saveReportToFile(user, report);

            // Đọc tệp vào bộ đệm byte
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

            // Trả về báo cáo dưới dạng tệp đính kèm
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + Paths.get(filePath).getFileName().toString())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate report", e);
        }
    }
}
