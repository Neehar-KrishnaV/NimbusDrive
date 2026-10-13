package com.neehar.nimbusdrive.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String uploadFile(MultipartFile file);
    public void deleteFile(String key);
    public byte[] downloadFile(String key);
}