package com.payflowx.authorization_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payflowx.authorization_service.dto.ApiResponse;
import com.payflowx.authorization_service.dto.AuthorizationRequest;
import com.payflowx.authorization_service.dto.AuthorizationResponse;
import com.payflowx.authorization_service.service.AuthorizationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/authorize")
@RequiredArgsConstructor
public class AuthorizationController {

	private final AuthorizationService authorizationService;

	@PostMapping
	public ResponseEntity<ApiResponse<AuthorizationResponse>> authorize(@RequestBody AuthorizationRequest request) {
		AuthorizationResponse response = authorizationService.authorize(request);
		if (response.isAuthorized()) {
			return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment authorized", response));
		}
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment rejected", response));
	}
}