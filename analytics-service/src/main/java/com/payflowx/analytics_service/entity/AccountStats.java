package com.payflowx.analytics_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountStats {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_stats_seq_gen")
	@SequenceGenerator(name = "account_stats_seq_gen", sequenceName = "account_stats_seq", allocationSize = 1)
	private Long id;

	@Column(nullable = false, unique = true, length = 20)
	private String accountId;

	@Column(nullable = false)
	@Builder.Default
	private Long totalSentCount = 0L;

	@Column(nullable = false)
	@Builder.Default
	private Long totalReceivedCount = 0L;

	@Column(nullable = false, precision = 19, scale = 4)
	@Builder.Default
	private BigDecimal totalSentAmount = BigDecimal.ZERO;

	@Column(nullable = false, precision = 19, scale = 4)
	@Builder.Default
	private BigDecimal totalReceivedAmount = BigDecimal.ZERO;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}