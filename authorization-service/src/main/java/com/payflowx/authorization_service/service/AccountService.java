package com.payflowx.authorization_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.payflowx.authorization_service.dto.AccountDTO;
import com.payflowx.authorization_service.dto.ApiResponse;
import com.payflowx.authorization_service.entity.Account;
import com.payflowx.authorization_service.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;

	public ResponseEntity<ApiResponse<Account>> createAccount(AccountDTO dto) {
		if (accountRepository.existsByAccountNumber(dto.getAccountNumber())) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(ApiResponse.error(HttpStatus.CONFLICT.value(), "Account number already exists", null));
		}

		Account account = Account.builder().accountNumber(dto.getAccountNumber()).userId(dto.getUserId())
				.balance(dto.getInitialBalance()).currency(dto.getCurrency()).dailyLimit(dto.getDailyLimit()).build();

		Account saved = accountRepository.save(account);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED.value(), "Account created", saved));
	}

	public ResponseEntity<ApiResponse<Account>> getAccount(String accountNumber) {
		Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
		if (accountOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Account not found", null));
		}
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Account found", accountOpt.get()));
	}

	public ResponseEntity<ApiResponse<List<Account>>> getAllAccounts() {
		List<Account> accounts = accountRepository.findAll();
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Accounts retrieved", accounts));
	}
}