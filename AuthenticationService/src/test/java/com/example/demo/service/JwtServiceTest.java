package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

	private final JwtService jwtService = new JwtService(
			"test-signing-secret-with-at-least-thirty-two-bytes",
			60_000);

	@Test
	void generatedTokenCanBeVerified() {
		String token = jwtService.generateToken("sample-user");

		assertTrue(jwtService.isValid(token));
		assertEquals("sample-user", jwtService.getUsername(token));
	}

	@Test
	void modifiedTokenIsRejected() {
		String token = jwtService.generateToken("sample-user") + "invalid";

		assertFalse(jwtService.isValid(token));
	}
}
