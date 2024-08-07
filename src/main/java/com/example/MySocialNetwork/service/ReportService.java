package com.example.MySocialNetwork.service;

import com.example.MySocialNetwork.entity.Report;
import com.example.MySocialNetwork.entity.User;
import com.example.MySocialNetwork.exception.User.ResourceNotFoundException;
import com.example.MySocialNetwork.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report createReport(Report report) {
        return reportRepository.save(report);
    }

    public Report updateReport(String publicId, Report reportDetails) {
        Report existingReport = reportRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with publicId: " + publicId));

        existingReport.setReportDate(reportDetails.getReportDate());
        existingReport.setNumPosts(reportDetails.getNumPosts());
        existingReport.setNumComments(reportDetails.getNumComments());
        existingReport.setNumFriends(reportDetails.getNumFriends());
        existingReport.setNumLikes(reportDetails.getNumLikes());
        existingReport.setNumLoves(reportDetails.getNumLoves());
        existingReport.setNumAngries(reportDetails.getNumAngries());
        existingReport.setNumSads(reportDetails.getNumSads());
        existingReport.setNumHahas(reportDetails.getNumHahas());

        return reportRepository.save(existingReport);
    }

    public void deleteReport(String publicId) {
        Report report = reportRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with publicId: " + publicId));

        reportRepository.delete(report);
    }

    public Report getReport(String publicId) {
        return reportRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with publicId: " + publicId));
    }

    public List<Report> getAllReportsByUser(User user) {
        return reportRepository.findAllByUser(user);
    }
}
