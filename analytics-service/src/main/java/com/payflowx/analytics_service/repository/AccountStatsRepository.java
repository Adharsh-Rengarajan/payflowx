package com.payflowx.analytics_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflowx.analytics_service.entity.AccountStats;

public interface AccountStatsRepository extends JpaRepository<AccountStats, Long> {
	Optional<AccountStats> findByAccountId(String accountId);
}