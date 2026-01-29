package com.payflowx.notification_service.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
	private String notificationType;
	private String paymentId;
	private String fromAccountId;
	private String toAccountId;
	private BigDecimal amount;
	private String currency;
	private String status;
	private String failureReason;
	private LocalDateTime timestamp;
}