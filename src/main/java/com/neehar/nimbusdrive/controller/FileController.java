package com.neehar.nimbusdrive.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.neehar.nimbusdrive.dto.FileResponse;
import com.neehar.nimbusdrive.service.FileService;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    
    @GetMapping("/me")
    public String me(Authentication authentication) {
        return authentication.getName();
    }
    
    @GetMapping
    public ResponseEntity<List<FileResponse>> getMyFiles(
            Authentication authentication) {

        return ResponseEntity.ok(
                fileService.getMyFiles(
                        authentication.getName()));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        fileService.uploadFile(
                file,
                authentication.getName());

        return ResponseEntity.ok("File uploaded successfully");
    }
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            Authentication authentication) {

        Resource resource = fileService.downloadFile(
                id,
                authentication.getName()
        );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\""
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(
            @PathVariable Long id,
            Authentication authentication) {

        fileService.deleteFile(
                id,
                authentication.getName());

        return ResponseEntity.ok(
                "File deleted successfully");
    }
    @GetMapping("/search")
    public ResponseEntity<List<FileResponse>> searchFiles(
            @RequestParam String name,
            Authentication authentication) {

        return ResponseEntity.ok(
                fileService.searchFiles(
                        authentication.getName(),
                        name));
    }
    @PostMapping("/share/{id}")
    public ResponseEntity<String> shareFile(
            @PathVariable Long id,
            Authentication authentication) {

        String token = fileService.shareFile(
                id,
                authentication.getName());

        return ResponseEntity.ok(
                "Share Link: http://localhost:8080/api/files/shared/"
                        + token);
    }
    @GetMapping("/shared/{token}")
    public ResponseEntity<Resource> downloadSharedFile(
            @PathVariable String token) {

        Resource resource = fileService.downloadSharedFile(token);

        String filename = resource.getFilename();

        if (filename == null || filename.isBlank()) {
            filename = "downloaded-file";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""
                )
                .body(resource);
    }
    @DeleteMapping("/share/{id}")
    public ResponseEntity<String> revokeShare(
            @PathVariable Long id,
            Authentication authentication) {

        fileService.revokeShare(
                id,
                authentication.getName());

        return ResponseEntity.ok(
                "Sharing revoked successfully");
    }
}