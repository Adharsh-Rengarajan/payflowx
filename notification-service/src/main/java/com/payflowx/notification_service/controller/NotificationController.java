package com.payflowx.notification_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payflowx.notification_service.dto.ApiResponse;
import com.payflowx.notification_service.entity.NotificationLog;
import com.payflowx.notification_service.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping("/health")
	public String health() {
		return "Notification Service is running";
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<NotificationLog>>> getAllNotifications() {
		return notificationService.getAllNotifications();
	}

	@GetMapping("/payment/{paymentId}")
	public ResponseEntity<ApiResponse<List<NotificationLog>>> getByPaymentId(@PathVariable String paymentId) {
		return notificationService.getByPaymentId(paymentId);
	}

	@GetMapping("/account/{accountId}")
	public ResponseEntity<ApiResponse<List<NotificationLog>>> getByAccountId(@PathVariable String accountId) {
		return notificationService.getByAccountId(accountId);
	}
}