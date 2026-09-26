package com.ferry.analytics.domain.report;

import java.math.BigDecimal;
import java.time.LocalDate;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record RevenueBucketProjection(
	LocalDate bucketStart,
	BigDecimal revenue,
	long orders){
}
