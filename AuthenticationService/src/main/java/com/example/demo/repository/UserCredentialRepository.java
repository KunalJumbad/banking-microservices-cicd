package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.UserCredential;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

	boolean existsByName(String name);

	boolean existsByEmail(String email);

	Optional<UserCredential> findByName(String name);

	Optional<UserCredential> findByEmail(String email);
}
