package com.example.demo.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.server.ServerWebExchange;

import com.example.demo.dto.TokenVerificationRequest;
import com.example.demo.dto.TokenVerificationResponse;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private static final String AUTHENTICATION_PATH = "/auth";
	private static final String AUTHENTICATED_USER_HEADER = "X-Authenticated-User";

	private final WebClient webClient;

	public JwtAuthenticationFilter(WebClient.Builder loadBalancedWebClientBuilder) {
		this.webClient = loadBalancedWebClientBuilder.build();
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
			return chain.filter(exchange);
		}

		String path = exchange.getRequest().getURI().getPath();
		if (path.equals(AUTHENTICATION_PATH) || path.startsWith(AUTHENTICATION_PATH + "/")) {
			return chain.filter(exchange);
		}

		String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		String token = extractBearerToken(authorization);
		if (token == null) {
			return reject(exchange, HttpStatus.UNAUTHORIZED);
		}

		return webClient.post()
				.uri("http://AuthenticationService/auth/verify")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(new TokenVerificationRequest(token))
				.retrieve()
				.bodyToMono(TokenVerificationResponse.class)
				.map(verification -> new VerificationOutcome(verification, true))
				.defaultIfEmpty(new VerificationOutcome(null, false))
				.onErrorResume(WebClientException.class, exception -> {
					logger.warn("Could not verify gateway request with AuthenticationService", exception);
					return Mono.just(new VerificationOutcome(null, false));
				})
				.flatMap(outcome -> {
					if (!outcome.serviceAvailable()) {
						return reject(exchange, HttpStatus.SERVICE_UNAVAILABLE);
					}
					TokenVerificationResponse verification = outcome.verification();
					if (verification == null || verification.username() == null) {
						return reject(exchange, HttpStatus.UNAUTHORIZED);
					}
					if (!verification.valid()) {
						return reject(exchange, HttpStatus.UNAUTHORIZED);
					}
					ServerWebExchange authenticatedExchange = exchange.mutate()
							.request(request -> request.headers(headers -> {
								headers.remove(AUTHENTICATED_USER_HEADER);
								headers.set(AUTHENTICATED_USER_HEADER, verification.username());
							}))
							.build();
					return chain.filter(authenticatedExchange);
				});
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	private String extractBearerToken(String authorization) {
		if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
			return null;
		}
		String token = authorization.substring(7).trim();
		return token.isEmpty() ? null : token;
	}

	private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status) {
		exchange.getResponse().setStatusCode(status);
		return exchange.getResponse().setComplete();
	}

	private record VerificationOutcome(
			TokenVerificationResponse verification,
			boolean serviceAvailable) {
	}
}
