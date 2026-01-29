package com.payflowx.analytics_service.dto;

import java.math.BigDecimal;

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
public class SummaryResponse {
	private Long totalPayments;
	private Long totalCompleted;
	private Long totalFailed;
	private Double successRate;
	private BigDecimal totalVolume;
	private BigDecimal failedVolume;
}