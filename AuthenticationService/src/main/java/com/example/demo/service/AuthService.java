package com.example.demo.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.TokenResponse;
import com.example.demo.dto.TokenVerificationResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.UserCredential;
import com.example.demo.exception.DuplicateCredentialException;
import com.example.demo.repository.UserCredentialRepository;

@Service
public class AuthService {

	private final UserCredentialRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(
			UserCredentialRepository repository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public UserResponse register(RegisterRequest request) {
		if (repository.existsByName(request.name())) {
			throw new DuplicateCredentialException("Username already exists");
		}
		if (repository.existsByEmail(request.email())) {
			throw new DuplicateCredentialException("Email already exists");
		}

		UserCredential user = new UserCredential(
				request.name(),
				request.email(),
				passwordEncoder.encode(request.password()));
		try {
			UserCredential savedUser = repository.saveAndFlush(user);
			return new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
		} catch (DataIntegrityViolationException exception) {
			throw new DuplicateCredentialException("Username or email already exists");
		}
	}

	@Transactional(readOnly = true)
	public TokenResponse issueToken(LoginRequest request) {
		UserCredential user = repository.findByName(request.username())
				.or(() -> repository.findByEmail(request.username()))
				.orElseThrow(AuthService::invalidCredentials);
		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw invalidCredentials();
		}
		return new TokenResponse(jwtService.generateToken(user.getName()), "Bearer");
	}

	public TokenVerificationResponse verifyToken(String token) {
		if (!jwtService.isValid(token)) {
			return new TokenVerificationResponse(false, null);
		}
		return new TokenVerificationResponse(true, jwtService.getUsername(token));
	}

	private static ResponseStatusException invalidCredentials() {
		return new ResponseStatusException(
				HttpStatus.UNAUTHORIZED,
				"Invalid username or password");
	}
}
