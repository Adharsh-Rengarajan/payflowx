package com.payflowx.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.payflowx.payment_service.dto.ApiResponse;
import com.payflowx.payment_service.dto.AuthorizationRequest;
import com.payflowx.payment_service.dto.AuthorizationResponse;

@FeignClient(name = "authorization-service")
public interface AuthorizationClient {

	@PostMapping("/api/authorize")
	ApiResponse<AuthorizationResponse> authorize(@RequestBody AuthorizationRequest request);
}