package com.example.demo.exception;

public class DuplicateCredentialException extends RuntimeException {

	public DuplicateCredentialException(String message) {
		super(message);
	}
}
