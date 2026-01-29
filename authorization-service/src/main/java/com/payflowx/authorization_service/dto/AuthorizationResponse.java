package com.payflowx.authorization_service.dto;

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
public class AuthorizationResponse {
	private boolean authorized;
	private boolean fromAccountValid;
	private boolean toAccountValid;
	private boolean sufficientBalance;
	private boolean withinDailyLimit;
	private String rejectionCode;
	private String rejectionReason;

	public static AuthorizationResponse approved() {
		return AuthorizationResponse.builder().authorized(true).fromAccountValid(true).toAccountValid(true)
				.sufficientBalance(true).withinDailyLimit(true).build();
	}

	public static AuthorizationResponse rejected(String code, String reason) {
		return AuthorizationResponse.builder().authorized(false).rejectionCode(code).rejectionReason(reason).build();
	}
}