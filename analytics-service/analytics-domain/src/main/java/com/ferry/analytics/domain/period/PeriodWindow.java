package com.ferry.analytics.domain.period;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;
import com.ferry.analytics.domain.report.ReportWindow;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PeriodWindow(
	Instant from,
	Instant to){
	public PeriodWindow{
		if(from == null || to == null){
			throw new InvalidAnalyticStateException("Period window bounds must not be null");
		}
		if(to.isBefore(from)){
			throw new InvalidAnalyticStateException("Period window must not end before it starts");
		}
	}

	public static PeriodWindow monthToDate(Instant now, ZoneId zone){
		LocalDate monthStart = LocalDate.ofInstant(now, zone).withDayOfMonth(1);
		return new PeriodWindow(monthStart.atStartOfDay(zone).toInstant(), now);
	}

	public static PeriodWindow upTo(ReportWindow window, Instant now){
		Instant end = window.toExclusiveInstant();
		return new PeriodWindow(window.fromInstant(), now.isBefore(end) ? now : end);
	}
}
