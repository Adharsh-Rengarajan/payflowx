package com.payflowx.api_gateway.filter;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.payflowx.api_gateway.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

	private final JwtUtil jwtUtil;

	private static final List<String> OPEN_ENDPOINTS = List.of("/api/auth");

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();

		if (isOpenEndpoint(path)) {
			log.debug("Open endpoint accessed: {}", path);
			return chain.filter(exchange);
		}

		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("Missing or invalid Authorization header for path: {}", path);
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		String token = authHeader.substring(7);

		if (!jwtUtil.validateToken(token)) {
			log.warn("Invalid JWT token for path: {}", path);
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		String userId = jwtUtil.extractUserId(token);
		String username = jwtUtil.extractUsername(token);

		ServerHttpRequest modifiedRequest = request.mutate().header("X-User-Id", userId).header("X-Username", username)
				.build();

		log.debug("JWT validated for user: {} accessing path: {}", username, path);

		return chain.filter(exchange.mutate().request(modifiedRequest).build());
	}

	private boolean isOpenEndpoint(String path) {
		return OPEN_ENDPOINTS.stream().anyMatch(path::startsWith);
	}

	@Override
	public int getOrder() {
		return -1;
	}
}