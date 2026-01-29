package com.payflowx.analytics_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payflowx.analytics_service.dto.ApiResponse;
import com.payflowx.analytics_service.dto.SummaryResponse;
import com.payflowx.analytics_service.entity.AccountStats;
import com.payflowx.analytics_service.entity.DailyPaymentStats;
import com.payflowx.analytics_service.event.PaymentEvent;
import com.payflowx.analytics_service.repository.AccountStatsRepository;
import com.payflowx.analytics_service.repository.DailyPaymentStatsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

	private final DailyPaymentStatsRepository dailyStatsRepository;
	private final AccountStatsRepository accountStatsRepository;

	@Transactional
	public void handlePaymentInitiated(PaymentEvent event) {
		LocalDate today = LocalDate.now();
		DailyPaymentStats stats = getOrCreateDailyStats(today);
		stats.setInitiatedCount(stats.getInitiatedCount() + 1);
		dailyStatsRepository.save(stats);
		log.info("Updated daily stats for initiated payment: {}", event.getPayload().getPaymentId());
	}

	@Transactional
	public void handlePaymentAuthorized(PaymentEvent event) {
		LocalDate today = LocalDate.now();
		DailyPaymentStats stats = getOrCreateDailyStats(today);
		stats.setAuthorizedCount(stats.getAuthorizedCount() + 1);
		dailyStatsRepository.save(stats);
		log.info("Updated daily stats for authorized payment: {}", event.getPayload().getPaymentId());
	}

	@Transactional
	public void handlePaymentCompleted(PaymentEvent event) {
		LocalDate today = LocalDate.now();
		DailyPaymentStats stats = getOrCreateDailyStats(today);
		stats.setCompletedCount(stats.getCompletedCount() + 1);
		stats.setTotalVolume(stats.getTotalVolume().add(event.getPayload().getAmount()));
		dailyStatsRepository.save(stats);

		updateAccountStats(event.getPayload().getFromAccountId(), event.getPayload().getAmount(), true);
		updateAccountStats(event.getPayload().getToAccountId(), event.getPayload().getAmount(), false);

		log.info("Updated stats for completed payment: {}", event.getPayload().getPaymentId());
	}

	@Transactional
	public void handlePaymentFailed(PaymentEvent event) {
		LocalDate today = LocalDate.now();
		DailyPaymentStats stats = getOrCreateDailyStats(today);
		stats.setFailedCount(stats.getFailedCount() + 1);
		stats.setFailedVolume(stats.getFailedVolume().add(event.getPayload().getAmount()));
		dailyStatsRepository.save(stats);
		log.info("Updated daily stats for failed payment: {}", event.getPayload().getPaymentId());
	}

	private DailyPaymentStats getOrCreateDailyStats(LocalDate date) {
		return dailyStatsRepository.findByStatsDate(date)
				.orElseGet(() -> dailyStatsRepository.save(DailyPaymentStats.builder().statsDate(date).build()));
	}

	private void updateAccountStats(String accountId, BigDecimal amount, boolean isSender) {
		AccountStats stats = accountStatsRepository.findByAccountId(accountId)
				.orElseGet(() -> accountStatsRepository.save(AccountStats.builder().accountId(accountId).build()));

		if (isSender) {
			stats.setTotalSentCount(stats.getTotalSentCount() + 1);
			stats.setTotalSentAmount(stats.getTotalSentAmount().add(amount));
		} else {
			stats.setTotalReceivedCount(stats.getTotalReceivedCount() + 1);
			stats.setTotalReceivedAmount(stats.getTotalReceivedAmount().add(amount));
		}
		accountStatsRepository.save(stats);
	}

	public ResponseEntity<ApiResponse<List<DailyPaymentStats>>> getAllDailyStats() {
		List<DailyPaymentStats> stats = dailyStatsRepository.findAll();
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Daily stats retrieved", stats));
	}

	public ResponseEntity<ApiResponse<DailyPaymentStats>> getDailyStats(LocalDate date) {
		return dailyStatsRepository.findByStatsDate(date)
				.map(stats -> ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Daily stats found", stats)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "No stats for this date", null)));
	}

	public ResponseEntity<ApiResponse<AccountStats>> getAccountStats(String accountId) {
		return accountStatsRepository.findByAccountId(accountId).map(
				stats -> ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Account stats found", stats)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "No stats for this account", null)));
	}

	public ResponseEntity<ApiResponse<List<AccountStats>>> getAllAccountStats() {
		List<AccountStats> stats = accountStatsRepository.findAll();
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Account stats retrieved", stats));
	}

	public ResponseEntity<ApiResponse<SummaryResponse>> getSummary() {
		List<DailyPaymentStats> allStats = dailyStatsRepository.findAll();

		long totalInitiated = allStats.stream().mapToLong(DailyPaymentStats::getInitiatedCount).sum();
		long totalCompleted = allStats.stream().mapToLong(DailyPaymentStats::getCompletedCount).sum();
		long totalFailed = allStats.stream().mapToLong(DailyPaymentStats::getFailedCount).sum();
		BigDecimal totalVolume = allStats.stream().map(DailyPaymentStats::getTotalVolume).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		BigDecimal failedVolume = allStats.stream().map(DailyPaymentStats::getFailedVolume).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		double successRate = totalInitiated > 0
				? BigDecimal.valueOf(totalCompleted).divide(BigDecimal.valueOf(totalInitiated), 4, RoundingMode.HALF_UP)
						.multiply(BigDecimal.valueOf(100)).doubleValue()
				: 0.0;

		SummaryResponse summary = SummaryResponse.builder().totalPayments(totalInitiated).totalCompleted(totalCompleted)
				.totalFailed(totalFailed).successRate(successRate).totalVolume(totalVolume).failedVolume(failedVolume)
				.build();

		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Summary retrieved", summary));
	}
}