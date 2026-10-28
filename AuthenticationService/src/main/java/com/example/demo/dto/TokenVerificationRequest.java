package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenVerificationRequest(
		@NotBlank(message = "Token is required")
		String token) {
}
