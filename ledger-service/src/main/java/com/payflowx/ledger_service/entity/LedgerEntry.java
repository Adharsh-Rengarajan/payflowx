package com.payflowx.ledger_service.entity;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ledger_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ledger_seq_gen")
	@SequenceGenerator(name = "ledger_seq_gen", sequenceName = "ledger_seq", allocationSize = 1)
	private Long id;

	@Column(nullable = false, length = 50)
	private String paymentId;

	@Column(nullable = false, length = 20)
	private String accountId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EntryType entryType;

	@Column(nullable = false, precision = 19, scale = 4)
	private BigDecimal amount;

	@Column(nullable = false, length = 3)
	private String currency;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private EntryStatus status = EntryStatus.PENDING;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime confirmedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public enum EntryType {
		DEBIT, CREDIT
	}

	public enum EntryStatus {
		PENDING, CONFIRMED, CANCELLED
	}
}
