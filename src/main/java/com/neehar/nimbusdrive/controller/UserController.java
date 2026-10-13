package com.neehar.nimbusdrive.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neehar.nimbusdrive.dto.ProfileResponse;
import com.neehar.nimbusdrive.service.UserProfileService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserProfileService userProfileService;

    public UserController(
            UserProfileService userProfileService) {

        this.userProfileService = userProfileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(
            Authentication authentication) {

        return ResponseEntity.ok(
                userProfileService.getProfile(
                        authentication.getName()));
    }
}