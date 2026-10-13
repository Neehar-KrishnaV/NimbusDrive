package com.neehar.nimbusdrive.service.impl;

import org.springframework.stereotype.Service;

import com.neehar.nimbusdrive.dto.DashboardResponse;
import com.neehar.nimbusdrive.entity.User;
import com.neehar.nimbusdrive.repository.FileMetadataRepository;
import com.neehar.nimbusdrive.repository.UserRepository;
import com.neehar.nimbusdrive.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final FileMetadataRepository fileMetadataRepository;

    public DashboardServiceImpl(
            UserRepository userRepository,
            FileMetadataRepository fileMetadataRepository) {

        this.userRepository = userRepository;
        this.fileMetadataRepository = fileMetadataRepository;
    }

    @Override
    public DashboardResponse getDashboard(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        long fileCount =
                fileMetadataRepository.countByOwner(user);

        double usagePercentage =
                ((double) user.getStorageUsed()
                        / user.getStorageLimit()) * 100;

        DashboardResponse response =
                new DashboardResponse();

        response.setEmail(user.getEmail());
        response.setStorageUsed(user.getStorageUsed());
        response.setStorageLimit(user.getStorageLimit());
        response.setFileCount(fileCount);
        response.setUsagePercentage(usagePercentage);

        return response;
    }
}