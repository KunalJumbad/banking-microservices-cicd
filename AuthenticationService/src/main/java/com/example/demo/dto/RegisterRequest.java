package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank(message = "Name (username) is required")
		@Size(min = 2, max = 60, message = "Name must be 2-60 characters")
		String name,

		@NotBlank(message = "Email is required")
		@Email(message = "Enter a valid email")
		@Size(max = 100, message = "Email cannot exceed 100 characters")
		String email,

		@NotBlank(message = "Password is required")
		@Size(min = 8, max = 72, message = "Password must be 8-72 characters")
		String password) {
}
