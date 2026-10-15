package com.ferry.analytics.domain.period;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record MonthToDateWindow(
	Instant from,
	Instant to){
	public MonthToDateWindow{
		if(from == null || to == null){
			throw new InvalidAnalyticStateException("Month-to-date window bounds must not be null");
		}
		if(to.isBefore(from)){
			throw new InvalidAnalyticStateException("Month-to-date window must not end before it starts");
		}
	}

	public static MonthToDateWindow endingAt(Instant now, ZoneId zone){
		LocalDate monthStart = LocalDate.ofInstant(now, zone).withDayOfMonth(1);
		return new MonthToDateWindow(monthStart.atStartOfDay(zone).toInstant(), now);
	}
}
