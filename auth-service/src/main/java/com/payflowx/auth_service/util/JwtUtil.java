package com.payflowx.auth_service.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.payflowx.auth_service.config.JwtConfig;
import com.payflowx.auth_service.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final JwtConfig jwtConfig;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
	}

	public String generateToken(User user) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

		return Jwts.builder().subject(user.getId().toString()).claim("username", user.getUsername()).issuedAt(now)
				.expiration(expiryDate).signWith(getSigningKey()).compact();
	}

	public String extractUserId(String token) {
		Claims claims = extractAllClaims(token);
		return claims.getSubject();
	}

	public String extractUsername(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get("username", String.class);
	}

	public boolean validateToken(String token) {
		try {
			extractAllClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}
}