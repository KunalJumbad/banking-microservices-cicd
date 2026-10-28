package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final Key signingKey;
	private final long expirationMillis;

	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-ms:1800000}") long expirationMillis) {
		byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (secretBytes.length < 32) {
			throw new IllegalArgumentException("JWT secret must be at least 32 bytes");
		}
		this.signingKey = Keys.hmacShaKeyFor(secretBytes);
		this.expirationMillis = expirationMillis;
	}

	public String generateToken(String username) {
		Date issuedAt = new Date();
		Date expiresAt = new Date(issuedAt.getTime() + expirationMillis);
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(issuedAt)
				.setExpiration(expiresAt)
				.signWith(signingKey, SignatureAlgorithm.HS256)
				.compact();
	}

	public Claims verifyToken(String token) {
		Jws<Claims> parsedToken = Jwts.parserBuilder()
				.setSigningKey(signingKey)
				.build()
				.parseClaimsJws(token);
		return parsedToken.getBody();
	}

	public boolean isValid(String token) {
		try {
			verifyToken(token);
			return true;
		} catch (JwtException | IllegalArgumentException exception) {
			return false;
		}
	}

	public String getUsername(String token) {
		return verifyToken(token).getSubject();
	}
}
