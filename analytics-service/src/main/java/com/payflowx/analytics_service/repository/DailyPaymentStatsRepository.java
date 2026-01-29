package com.payflowx.analytics_service.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflowx.analytics_service.entity.DailyPaymentStats;

public interface DailyPaymentStatsRepository extends JpaRepository<DailyPaymentStats, Long> {
	Optional<DailyPaymentStats> findByStatsDate(LocalDate statsDate);
}