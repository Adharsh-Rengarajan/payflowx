package com.payflowx.authorization_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflowx.authorization_service.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByAccountNumber(String accountNumber);

	boolean existsByAccountNumber(String accountNumber);
}