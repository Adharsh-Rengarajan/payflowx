package com.payflowx.payment_service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
	private String idempotencyKey;
	private String fromAccountId;
	private String toAccountId;
	private BigDecimal amount;
	private String currency;
	private String description;
}
