package com.payflowx.notification_service.entity;

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
@Table(name = "notification_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq_gen")
	@SequenceGenerator(name = "notification_seq_gen", sequenceName = "notification_seq", allocationSize = 1)
	private Long id;

	@Column(nullable = false, length = 50)
	private String paymentId;

	@Column(nullable = false, length = 30)
	private String notificationType;

	@Column(nullable = false, length = 20)
	private String recipientAccountId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RecipientType recipientType;

	@Column(nullable = false, precision = 19, scale = 4)
	private BigDecimal amount;

	@Column(nullable = false, length = 3)
	private String currency;

	@Column(length = 500)
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private NotificationStatus status = NotificationStatus.SENT;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public enum RecipientType {
		SENDER, RECEIVER
	}

	public enum NotificationStatus {
		PENDING, SENT, FAILED
	}
}
