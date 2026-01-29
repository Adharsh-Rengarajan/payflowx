package com.payflowx.analytics_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payflowx.analytics_service.dto.ApiResponse;
import com.payflowx.analytics_service.dto.SummaryResponse;
import com.payflowx.analytics_service.entity.AccountStats;
import com.payflowx.analytics_service.entity.DailyPaymentStats;
import com.payflowx.analytics_service.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

	private final AnalyticsService analyticsService;

	@GetMapping("/health")
	public String health() {
		return "Analytics Service is running";
	}

	@GetMapping("/summary")
	public ResponseEntity<ApiResponse<SummaryResponse>> getSummary() {
		return analyticsService.getSummary();
	}

	@GetMapping("/daily")
	public ResponseEntity<ApiResponse<List<DailyPaymentStats>>> getAllDailyStats() {
		return analyticsService.getAllDailyStats();
	}

	@GetMapping("/daily/{date}")
	public ResponseEntity<ApiResponse<DailyPaymentStats>> getDailyStats(
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return analyticsService.getDailyStats(date);
	}

	@GetMapping("/accounts")
	public ResponseEntity<ApiResponse<List<AccountStats>>> getAllAccountStats() {
		return analyticsService.getAllAccountStats();
	}

	@GetMapping("/account/{accountId}")
	public ResponseEntity<ApiResponse<AccountStats>> getAccountStats(@PathVariable String accountId) {
		return analyticsService.getAccountStats(accountId);
	}
}