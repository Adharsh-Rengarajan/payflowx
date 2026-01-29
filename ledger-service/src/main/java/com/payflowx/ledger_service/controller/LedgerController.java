package com.payflowx.ledger_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payflowx.ledger_service.dto.ApiResponse;
import com.payflowx.ledger_service.entity.LedgerEntry;
import com.payflowx.ledger_service.service.LedgerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {

	private final LedgerService ledgerService;

	@GetMapping("/health")
	public String health() {
		return "Ledger Service is running";
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<LedgerEntry>>> getAllEntries() {
		return ledgerService.getAllEntries();
	}

	@GetMapping("/payment/{paymentId}")
	public ResponseEntity<ApiResponse<List<LedgerEntry>>> getByPaymentId(@PathVariable String paymentId) {
		return ledgerService.getEntriesByPaymentId(paymentId);
	}

	@GetMapping("/account/{accountId}")
	public ResponseEntity<ApiResponse<List<LedgerEntry>>> getByAccountId(@PathVariable String accountId) {
		return ledgerService.getEntriesByAccountId(accountId);
	}
}