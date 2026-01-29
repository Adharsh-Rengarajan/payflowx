package com.payflowx.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payflowx.auth_service.dto.LoginRequestDTO;
import com.payflowx.auth_service.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private AuthService authService;

	@GetMapping("/")
	public String health() {
		return "Working";
	}

	@PostMapping("/")
	public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
		return authService.login(loginRequestDTO);
	}
}
