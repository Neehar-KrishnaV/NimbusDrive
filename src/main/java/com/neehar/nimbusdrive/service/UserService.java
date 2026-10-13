package com.neehar.nimbusdrive.service;

import com.neehar.nimbusdrive.dto.LoginRequest;
import com.neehar.nimbusdrive.dto.RegisterRequest;
import com.neehar.nimbusdrive.entity.enums.Role;

public interface UserService {
	void register(RegisterRequest request);
	String login(LoginRequest request);
	
}
