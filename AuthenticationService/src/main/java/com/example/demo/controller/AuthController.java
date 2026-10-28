package com.example.demo.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.TokenResponse;
import com.example.demo.dto.TokenVerificationRequest;
import com.example.demo.dto.TokenVerificationResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.service.AuthService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@PostMapping("/token")
	public TokenResponse issueToken(@Valid @RequestBody LoginRequest request) {
		return authService.issueToken(request);
	}

	@PostMapping("/verify")
	public TokenVerificationResponse verify(@Valid @RequestBody TokenVerificationRequest request) {
		return authService.verifyToken(request.token());
	}

	@GetMapping("/validate")
	public TokenVerificationResponse validate(@RequestParam("token") String token) {
		return authService.verifyToken(token);
	}
}
