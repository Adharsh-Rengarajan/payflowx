package com.payflowx.authorization_service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
	private String accountNumber;
	private Long userId;
	private BigDecimal initialBalance;
	private String currency;
	private BigDecimal dailyLimit;
}