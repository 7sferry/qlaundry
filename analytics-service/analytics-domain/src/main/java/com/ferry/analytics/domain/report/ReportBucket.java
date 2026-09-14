package com.ferry.analytics.domain.report;

import java.time.LocalDate;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public enum ReportBucket{
	DAY,
	WEEK,
	MONTH,
	;

	public LocalDate next(LocalDate start){
		return switch(this){
			case DAY -> start.plusDays(1);
			case WEEK -> start.plusWeeks(1);
			case MONTH -> start.plusMonths(1);
		};
	}

}
