package com.ferry.analytics.domain.report;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PeriodTotalsProjection(
	long orders,
	BigDecimal revenue){
}
