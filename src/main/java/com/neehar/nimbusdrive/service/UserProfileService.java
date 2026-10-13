package com.neehar.nimbusdrive.service;

import com.neehar.nimbusdrive.dto.ProfileResponse;

public interface UserProfileService {

    ProfileResponse getProfile(String email);
}