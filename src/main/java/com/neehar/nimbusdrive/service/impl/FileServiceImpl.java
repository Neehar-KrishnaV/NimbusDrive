package com.neehar.nimbusdrive.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.neehar.nimbusdrive.dto.FileResponse;
import com.neehar.nimbusdrive.entity.User;
import com.neehar.nimbusdrive.entity.FileMetadata;

import com.neehar.nimbusdrive.entity.FileMetadata;
import com.neehar.nimbusdrive.entity.User;

import com.neehar.nimbusdrive.repository.FileMetadataRepository;
import com.neehar.nimbusdrive.repository.UserRepository;
import com.neehar.nimbusdrive.service.FileService;
import com.neehar.nimbusdrive.service.S3Service;

@Service
public class FileServiceImpl implements FileService {

    private final FileMetadataRepository fileMetadataRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public FileServiceImpl(
            FileMetadataRepository fileMetadataRepository,
            UserRepository userRepository,
            S3Service s3Service) {

        this.fileMetadataRepository = fileMetadataRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }

    @Override
    public void uploadFile(MultipartFile file, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        long newStorageUsage =
                user.getStorageUsed() + file.getSize();

        if (newStorageUsage > user.getStorageLimit()) {
            throw new RuntimeException("Storage limit exceeded");
        }

        String uniqueFileName =
                UUID.randomUUID() + "_" +
                file.getOriginalFilename();


        try {

            String s3Key = s3Service.uploadFile(file);

            FileMetadata metadata = new FileMetadata();

            metadata.setFileName(file.getOriginalFilename());
            metadata.setFileType(file.getContentType());
            metadata.setFileSize(file.getSize());
            metadata.setFilePath(s3Key);
            metadata.setUploadedAt(LocalDateTime.now());
            metadata.setOwner(user);

            fileMetadataRepository.save(metadata);

            user.setStorageUsed(newStorageUsage);
            userRepository.save(user);

        } catch (Exception e) {

            throw new RuntimeException(
                    "File upload failed: " + e.getMessage());
        }
    }
    @Override
    public List<FileResponse> getMyFiles(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<FileMetadata> files =
                fileMetadataRepository.findByOwner(user);

        return files.stream()
                .map(file -> {

                    FileResponse response =
                            new FileResponse();

                    response.setId(file.getId());
                    response.setFileName(file.getFileName());
                    response.setFileType(file.getFileType());
                    response.setFileSize(file.getFileSize());
                    response.setUploadedAt(file.getUploadedAt());

                    return response;

                })
                .collect(Collectors.toList());
    }
    @Override
    public Resource downloadFile(Long fileId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        FileMetadata fileMetadata =
                fileMetadataRepository
                        .findByIdAndOwner(fileId, user)
                        .orElseThrow(() ->
                                new RuntimeException("File not found"));

        try {

            byte[] data =
                    s3Service.downloadFile(
                            fileMetadata.getFilePath());

            return new ByteArrayResource(data);

        } catch (Exception e) {
            e.printStackTrace();   // VERY IMPORTANT
            throw new RuntimeException("Error downloading file", e);
        }
    }
    @Override
    public void deleteFile(Long fileId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        FileMetadata fileMetadata =
                fileMetadataRepository
                        .findByIdAndOwner(fileId, user)
                        .orElseThrow(() ->
                                new RuntimeException("File not found"));

        s3Service.deleteFile(
                fileMetadata.getFilePath());

        user.setStorageUsed(
                user.getStorageUsed()
                - fileMetadata.getFileSize());

        userRepository.save(user);

        fileMetadataRepository.delete(fileMetadata);
    }
    @Override
    public List<FileResponse> searchFiles(
            String email,
            String fileName) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<FileMetadata> files =
                fileMetadataRepository
                        .findByOwnerAndFileNameContainingIgnoreCase(
                                user,
                                fileName);

        return files.stream()
                .map(file -> {

                    FileResponse response =
                            new FileResponse();

                    response.setId(file.getId());
                    response.setFileName(file.getFileName());
                    response.setFileType(file.getFileType());
                    response.setFileSize(file.getFileSize());
                    response.setUploadedAt(file.getUploadedAt());

                    return response;

                })
                .toList();
    }
    @Override
    public String generateShareLink(
            Long fileId,
            String email) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException("User not found"));

        FileMetadata file =
                fileMetadataRepository
                        .findByIdAndOwner(fileId, user)
                        .orElseThrow(
                                () -> new RuntimeException("File not found"));

        String token =
                UUID.randomUUID().toString();

        file.setShared(true);
        file.setShareToken(token);

        fileMetadataRepository.save(file);

        return "http://localhost:8080/api/files/shared/"
                + token;
    }
    @Override
    public Resource downloadSharedFile(String token) {

        FileMetadata file = fileMetadataRepository
                .findByShareToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid or revoked share link"));

        if (!Boolean.TRUE.equals(file.getShared())) {
            throw new RuntimeException(
                    "This share link has been revoked");
        }

        byte[] data = s3Service.downloadFile(file.getFilePath());

        return new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return file.getFileName();
            }
        };
    }
    @Override
    public String shareFile(Long fileId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        FileMetadata file = fileMetadataRepository
                .findByIdAndOwner(fileId, user)
                .orElseThrow(() ->
                        new RuntimeException("File not found"));

        String token = UUID.randomUUID().toString();

        file.setShareToken(token);
        file.setShared(true);

        fileMetadataRepository.save(file);

        return token;
    }
    
    @Override
    public void revokeShare(Long fileId, String userEmail) {

        FileMetadata file = fileMetadataRepository
                .findById(fileId)
                .orElseThrow(() ->
                        new RuntimeException("File not found"));

        // Ensure users can revoke sharing only for their own files.
        if (!file.getOwner().getEmail().equals(userEmail)) {
            throw new RuntimeException(
                    "You are not authorized to revoke sharing for this file");
        }

        // Disable sharing and invalidate the existing link.
        file.setShared(false);
        file.setShareToken(null);

        fileMetadataRepository.save(file);
    }
}