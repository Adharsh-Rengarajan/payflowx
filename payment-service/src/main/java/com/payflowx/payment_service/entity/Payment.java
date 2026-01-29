package com.payflowx.payment_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payments_seq_gen")
	@SequenceGenerator(name = "payments_seq_gen", sequenceName = "payments_seq", allocationSize = 1)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String paymentId;

	@Column(nullable = false, unique = true, length = 100)
	private String idempotencyKey;

	@Column(nullable = false, length = 20)
	private String fromAccountId;

	@Column(nullable = false, length = 20)
	private String toAccountId;

	@Column(nullable = false, precision = 19, scale = 4)
	private BigDecimal amount;

	@Column(nullable = false, length = 3)
	private String currency;

	@Column(length = 255)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private PaymentStatus status = PaymentStatus.INITIATED;

	@Column(length = 50)
	private String failureCode;

	@Column(length = 255)
	private String failureReason;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	private LocalDateTime authorizedAt;

	private LocalDateTime completedAt;

	private LocalDateTime failedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public enum PaymentStatus {
		INITIATED, AUTHORIZED, COMPLETED, FAILED, CANCELLED
	}
}