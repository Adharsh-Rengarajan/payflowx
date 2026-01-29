package com.payflowx.ledger_service.event;

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
public class PaymentEvent {
	private String eventId;
	private String eventType;
	private LocalDateTime timestamp;
	private PaymentEventPayload payload;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PaymentEventPayload {
		private String paymentId;
		private String idempotencyKey;
		private String fromAccountId;
		private String toAccountId;
		private BigDecimal amount;
		private String currency;
		private String status;
		private String failureCode;
		private String failureReason;
	}
}