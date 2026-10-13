package com.neehar.nimbusdrive.service.impl;

import org.springframework.stereotype.Service;

import com.neehar.nimbusdrive.dto.ProfileResponse;
import com.neehar.nimbusdrive.entity.User;
import com.neehar.nimbusdrive.repository.UserRepository;
import com.neehar.nimbusdrive.service.UserProfileService;

@Service
public class UserProfileServiceImpl
        implements UserProfileService {

    private final UserRepository userRepository;

    public UserProfileServiceImpl(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public ProfileResponse getProfile(
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        ProfileResponse response =
                new ProfileResponse();

        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setStorageUsed(user.getStorageUsed());
        response.setStorageLimit(user.getStorageLimit());

        return response;
    }
}