package com.neehar.nimbusdrive.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.neehar.nimbusdrive.dto.LoginRequest;
import com.neehar.nimbusdrive.dto.RegisterRequest;
import com.neehar.nimbusdrive.entity.User;
import com.neehar.nimbusdrive.entity.enums.Role;
import com.neehar.nimbusdrive.repository.UserRepository;
import com.neehar.nimbusdrive.security.JwtService;
import com.neehar.nimbusdrive.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtService jwtService;
	
	public UserServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
this.userRepository = userRepository;
this.passwordEncoder = passwordEncoder;
this.jwtService = jwtService;
}

	@Override
	public void register(RegisterRequest request) {

	    Optional<User> existingUser =
	            userRepository.findByEmail(request.getEmail());

	    if (existingUser.isPresent()) {
	        throw new RuntimeException("Email already registered");
	    }

	    User user = new User();

	    user.setFullName(request.getFullName());
	    user.setEmail(request.getEmail());

	    user.setPassword(passwordEncoder.encode(request.getPassword()));

	    user.setRole(Role.USER);

	    user.setStorageUsed(0L);
	    user.setStorageLimit(5L * 1024 * 1024 * 1024);

	    userRepository.save(user);
	}
	
	@Override
	public String login(LoginRequest request) {

	    User user = userRepository.findByEmail(request.getEmail())
	            .orElseThrow(() ->
	                    new RuntimeException("User not found"));

	    boolean matches = passwordEncoder.matches(
	            request.getPassword(),
	            user.getPassword());

	    if (!matches) {
	        throw new RuntimeException("Invalid password");
	    }

	    return jwtService.generateToken(user.getEmail());
	}

}
