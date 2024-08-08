package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.Friend;
import com.example.MySocialNetwork.entity.Report;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.repository.CommentRepository;
import com.example.MySocialNetwork.repository.FriendRepository;
import com.example.MySocialNetwork.repository.PostRepository;
import com.example.MySocialNetwork.repository.UserRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class ReportService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FriendRepository friendRepository;

    @Autowired
    public ReportService(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository, FriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.friendRepository = friendRepository;
    }

    public Report generateReport(User user) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);

        int numPosts = countPosts(user, oneWeekAgo);
        int numComments = countComments(user, oneWeekAgo);
        int numFriends = countFriends(user, oneWeekAgo);
        int numLikes = countLikes(user, oneWeekAgo);

        return createReport(user, numPosts, numComments, numFriends, numLikes);
    }

    private int countPosts(User user, LocalDateTime oneWeekAgo) {
        return postRepository.countByUserAndCreatedAtAfter(user, oneWeekAgo);
    }

    private int countComments(User user, LocalDateTime oneWeekAgo) {
        return commentRepository.countByUserAndCreatedAtAfter(user, oneWeekAgo);
    }

    private int countFriends(User user, LocalDateTime oneWeekAgo) {
        return friendRepository.countByUserAndStatusAndCreatedAtAfter(user, Friend.Status.ACCEPTED, oneWeekAgo);
    }

    private int countLikes(User user, LocalDateTime oneWeekAgo) {
        return postRepository.countLikesByUserAndCreatedAtAfter(user, oneWeekAgo);
    }

    private Report createReport(User user, int numPosts, int numComments, int numFriends, int numLikes) {
        Report report = new Report();
        report.setUser(user);
        report.setReportDate(LocalDate.now());
        report.setNumPosts(numPosts);
        report.setNumComments(numComments);
        report.setNumFriends(numFriends);
        report.setNumLikes(numLikes);
        return report;
    }

    public String saveReportToFile(User user, Report report) throws IOException {
        String filePath = createFilePath(user);
        createDirectoryIfNotExists(filePath);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("User Report");
            createHeaderRow(sheet);
            populateDataRow(report, sheet);

            writeFile(filePath, workbook);
        }

        return filePath;
    }

    private String createFilePath(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String username = user.getUsername();
        String baseDir = "statics/report";
        String userDir = baseDir + "/" + username;
        String fileName = timestamp + "_report.xlsx";
        return userDir + "/" + fileName;
    }

    private void createDirectoryIfNotExists(String filePath) {
        File directory = new File(filePath).getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Report Date");
        headerRow.createCell(1).setCellValue("Number of Posts");
        headerRow.createCell(2).setCellValue("Number of Comments");
        headerRow.createCell(3).setCellValue("Number of New Friends");
        headerRow.createCell(4).setCellValue("Number of Likes");
    }

    private void populateDataRow(Report report, Sheet sheet) {
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(report.getReportDate().toString());
        dataRow.createCell(1).setCellValue(report.getNumPosts());
        dataRow.createCell(2).setCellValue(report.getNumComments());
        dataRow.createCell(3).setCellValue(report.getNumFriends());
        dataRow.createCell(4).setCellValue(report.getNumLikes());
    }

    private void writeFile(String filePath, Workbook workbook) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }
    }
}
