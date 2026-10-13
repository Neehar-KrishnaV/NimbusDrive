package com.neehar.nimbusdrive.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import com.neehar.nimbusdrive.dto.FileResponse;

public interface FileService {

    void uploadFile(MultipartFile file, String email);

    List<FileResponse> getMyFiles(String email);
    Resource downloadFile(Long fileId, String email);
    void deleteFile(Long fileId, String email);
    List<FileResponse> searchFiles(
            String email,
            String fileName);
    String generateShareLink(
            Long fileId,
            String email);

    Resource downloadSharedFile(
            String token);
    
    String shareFile(Long fileId, String email);
    void revokeShare(Long fileId, String userEmail);
}