package com.neehar.nimbusdrive.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.neehar.nimbusdrive.dto.LoginRequest;
import com.neehar.nimbusdrive.dto.RegisterRequest;
import com.neehar.nimbusdrive.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

        userService.register(request);

        return ResponseEntity.ok("User Registered Successfully");
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request) {

        String token = userService.login(request);

        return ResponseEntity.ok(token);
    }
    
    @GetMapping("/test")
    public String test() {
        return "Auth Controller Working";
    }
}