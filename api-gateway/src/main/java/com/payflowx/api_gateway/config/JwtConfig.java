package com.payflowx.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class JwtConfig {

	@Value("${jwt.secret}")
	private String secret;
}