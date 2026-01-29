package com.payflowx.authorization_service.entity;

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
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounts_seq_gen")
	@SequenceGenerator(name = "accounts_seq_gen", sequenceName = "accounts_seq", allocationSize = 1)
	private Long id;

	@Column(nullable = false, unique = true, length = 20)
	private String accountNumber;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false, precision = 19, scale = 4)
	private BigDecimal balance;

	@Column(nullable = false, length = 3)
	private String currency;

	@Column(nullable = false, precision = 19, scale = 4)
	private BigDecimal dailyLimit;

	@Column(nullable = false, precision = 19, scale = 4)
	@Builder.Default
	private BigDecimal dailyUsed = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private AccountStatus status = AccountStatus.ACTIVE;

	@Column(nullable = false, updatable = false)
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

	public enum AccountStatus {
		ACTIVE, FROZEN, CLOSED
	}
}
