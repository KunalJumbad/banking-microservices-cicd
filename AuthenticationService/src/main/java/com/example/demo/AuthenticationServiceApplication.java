package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entity.UserCredential;
import com.example.demo.repository.UserCredentialRepository;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthenticationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner initDefaultUser(UserCredentialRepository repository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (!repository.existsByName("admin") && !repository.existsByEmail("admin@northstar.com")) {
				UserCredential defaultAdmin = new UserCredential(
						"admin",
						"admin@northstar.com",
						passwordEncoder.encode("welcome123")
				);
				repository.save(defaultAdmin);
			}
		};
	}
}
