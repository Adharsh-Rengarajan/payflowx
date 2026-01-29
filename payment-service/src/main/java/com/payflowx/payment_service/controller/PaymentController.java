package com.payflowx.payment_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payflowx.payment_service.dto.ApiResponse;
import com.payflowx.payment_service.dto.PaymentRequest;
import com.payflowx.payment_service.dto.PaymentResponse;
import com.payflowx.payment_service.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(@RequestBody PaymentRequest request) {
		return paymentService.initiatePayment(request);
	}

	@GetMapping("/{paymentId}")
	public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable String paymentId) {
		return paymentService.getPayment(paymentId);
	}

	@GetMapping("/health")
	public String health() {
		return "Payment Service is running";
	}
}