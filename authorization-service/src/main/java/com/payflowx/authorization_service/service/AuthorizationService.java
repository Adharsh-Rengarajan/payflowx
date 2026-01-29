package com.payflowx.authorization_service.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.payflowx.authorization_service.dto.AuthorizationRequest;
import com.payflowx.authorization_service.dto.AuthorizationResponse;
import com.payflowx.authorization_service.entity.Account;
import com.payflowx.authorization_service.entity.Account.AccountStatus;
import com.payflowx.authorization_service.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

	private final AccountRepository accountRepository;

	public AuthorizationResponse authorize(AuthorizationRequest request) {
		Optional<Account> fromAccountOpt = accountRepository.findByAccountNumber(request.getFromAccountId());
		if (fromAccountOpt.isEmpty()) {
			return AuthorizationResponse.builder().authorized(false).fromAccountValid(false)
					.rejectionCode("FROM_ACCOUNT_NOT_FOUND").rejectionReason("Source account does not exist").build();
		}

		Optional<Account> toAccountOpt = accountRepository.findByAccountNumber(request.getToAccountId());
		if (toAccountOpt.isEmpty()) {
			return AuthorizationResponse.builder().authorized(false).fromAccountValid(true).toAccountValid(false)
					.rejectionCode("TO_ACCOUNT_NOT_FOUND").rejectionReason("Destination account does not exist")
					.build();
		}

		Account fromAccount = fromAccountOpt.get();
		Account toAccount = toAccountOpt.get();

		if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
			return AuthorizationResponse.builder().authorized(false).fromAccountValid(true).toAccountValid(true)
					.rejectionCode("FROM_ACCOUNT_NOT_ACTIVE")
					.rejectionReason("Source account is " + fromAccount.getStatus()).build();
		}

		if (toAccount.getStatus() != AccountStatus.ACTIVE) {
			return AuthorizationResponse.builder().authorized(false).fromAccountValid(true).toAccountValid(true)
					.rejectionCode("TO_ACCOUNT_NOT_ACTIVE")
					.rejectionReason("Destination account is " + toAccount.getStatus()).build();
		}

		if (!fromAccount.getCurrency().equals(request.getCurrency())) {
			return AuthorizationResponse.builder().authorized(false).fromAccountValid(true).toAccountValid(true)
					.rejectionCode("CURRENCY_MISMATCH").rejectionReason("Account currency " + fromAccount.getCurrency()
							+ " does not match request currency " + request.getCurrency())
					.build();
		}

		if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
			return AuthorizationResponse.builder().authorized(false).fromAccountValid(true).toAccountValid(true)
					.sufficientBalance(false).rejectionCode("INSUFFICIENT_BALANCE")
					.rejectionReason("Available balance is insufficient").build();
		}

		BigDecimal newDailyUsed = fromAccount.getDailyUsed().add(request.getAmount());
		if (newDailyUsed.compareTo(fromAccount.getDailyLimit()) > 0) {
			return AuthorizationResponse.builder().authorized(false).fromAccountValid(true).toAccountValid(true)
					.sufficientBalance(true).withinDailyLimit(false).rejectionCode("DAILY_LIMIT_EXCEEDED")
					.rejectionReason("Transaction would exceed daily limit").build();
		}

		return AuthorizationResponse.approved();
	}
}
