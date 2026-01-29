package com.payflowx.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationResponse {
	private boolean authorized;
	private boolean fromAccountValid;
	private boolean toAccountValid;
	private boolean sufficientBalance;
	private boolean withinDailyLimit;
	private String rejectionCode;
	private String rejectionReason;
}