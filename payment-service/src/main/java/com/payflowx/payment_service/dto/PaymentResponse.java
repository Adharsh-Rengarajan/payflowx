package com.payflowx.payment_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.payflowx.payment_service.entity.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
	private String paymentId;
	private String status;
	private String fromAccountId;
	private String toAccountId;
	private BigDecimal amount;
	private String currency;
	private String description;
	private String failureCode;
	private String failureReason;
	private LocalDateTime createdAt;
	private LocalDateTime completedAt;

	public static PaymentResponse fromEntity(Payment payment) {
		return PaymentResponse.builder().paymentId(payment.getPaymentId()).status(payment.getStatus().name())
				.fromAccountId(payment.getFromAccountId()).toAccountId(payment.getToAccountId())
				.amount(payment.getAmount()).currency(payment.getCurrency()).description(payment.getDescription())
				.failureCode(payment.getFailureCode()).failureReason(payment.getFailureReason())
				.createdAt(payment.getCreatedAt()).completedAt(payment.getCompletedAt()).build();
	}
}
