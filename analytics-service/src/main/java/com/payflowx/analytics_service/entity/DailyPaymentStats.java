package com.payflowx.analytics_service.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "daily_payment_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyPaymentStats {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_stats_seq_gen")
	@SequenceGenerator(name = "daily_stats_seq_gen", sequenceName = "daily_stats_seq", allocationSize = 1)
	private Long id;

	@Column(nullable = false, unique = true)
	private LocalDate statsDate;

	@Column(nullable = false)
	@Builder.Default
	private Long initiatedCount = 0L;

	@Column(nullable = false)
	@Builder.Default
	private Long authorizedCount = 0L;

	@Column(nullable = false)
	@Builder.Default
	private Long completedCount = 0L;

	@Column(nullable = false)
	@Builder.Default
	private Long failedCount = 0L;

	@Column(nullable = false, precision = 19, scale = 4)
	@Builder.Default
	private BigDecimal totalVolume = BigDecimal.ZERO;

	@Column(nullable = false, precision = 19, scale = 4)
	@Builder.Default
	private BigDecimal failedVolume = BigDecimal.ZERO;
}