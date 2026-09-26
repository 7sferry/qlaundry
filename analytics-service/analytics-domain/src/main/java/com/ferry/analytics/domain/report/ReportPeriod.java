package com.ferry.analytics.domain.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
@RequiredArgsConstructor
public enum ReportPeriod{
	WEEK(ReportBucket.DAY, "EEE dd"),
	MONTH(ReportBucket.DAY, "dd MMM"),
	QUARTER(ReportBucket.WEEK, "dd MMM"),
	YEAR(ReportBucket.MONTH, "MMM"),
	;

	private final ReportBucket bucket;
	private final String labelPattern;

	public ReportWindow windowFor(LocalDate today, ZoneId zone){
		return switch(this){
			case WEEK -> new ReportWindow(this, today.minusDays(6), today.plusDays(1), zone);
			case MONTH -> new ReportWindow(this, today.withDayOfMonth(1), today.withDayOfMonth(1).plusMonths(1), zone);
			case QUARTER -> new ReportWindow(this,
					today.minusMonths(3).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), today.plusDays(1),
					zone);
			case YEAR -> new ReportWindow(this, today.withDayOfMonth(1).minusMonths(11),
					today.withDayOfMonth(1).plusMonths(1), zone);
		};
	}

	public String label(LocalDate bucketStart){
		return DateTimeFormatter.ofPattern(labelPattern, Locale.ENGLISH).format(bucketStart);
	}

}
