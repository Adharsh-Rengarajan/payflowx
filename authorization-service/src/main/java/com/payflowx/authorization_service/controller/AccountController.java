package com.payflowx.authorization_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payflowx.authorization_service.dto.AccountDTO;
import com.payflowx.authorization_service.dto.ApiResponse;
import com.payflowx.authorization_service.entity.Account;
import com.payflowx.authorization_service.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@PostMapping
	public ResponseEntity<ApiResponse<Account>> createAccount(@RequestBody AccountDTO dto) {
		return accountService.createAccount(dto);
	}

	@GetMapping("/{accountNumber}")
	public ResponseEntity<ApiResponse<Account>> getAccount(@PathVariable String accountNumber) {
		return accountService.getAccount(accountNumber);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<Account>>> getAllAccounts() {
		return accountService.getAllAccounts();
	}
}