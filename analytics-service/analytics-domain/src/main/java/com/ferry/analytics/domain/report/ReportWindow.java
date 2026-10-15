package com.ferry.analytics.domain.report;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record ReportWindow(
	ReportPeriod period,
	LocalDate from,
	LocalDate toExclusive,
	ZoneId zone){
	public ReportWindow{
		if(period == null || from == null || toExclusive == null || zone == null){
			throw new InvalidAnalyticStateException("Report period, bounds and zone must not be null");
		}
		if(!from.isBefore(toExclusive)){
			throw new InvalidAnalyticStateException("Report start must be before its end");
		}
	}

	public ReportBucket bucket(){
		return period.getBucket();
	}

	public Instant fromInstant(){
		return from.atStartOfDay(zone).toInstant();
	}

	public Instant toExclusiveInstant(){
		return toExclusive.atStartOfDay(zone).toInstant();
	}

	public ReportWindow previous(){
		LocalDate previousFrom = switch(period){
			case MONTH -> from.minusMonths(1);
			case YEAR -> from.minusMonths(12);
			case WEEK, QUARTER -> from.minusDays(ChronoUnit.DAYS.between(from, toExclusive));
		};
		return new ReportWindow(period, previousFrom, from, zone);
	}

	public List<LocalDate> bucketStarts(){
		List<LocalDate> starts = new ArrayList<>();
		for(LocalDate start = from; start.isBefore(toExclusive); start = bucket().next(start)){
			starts.add(start);
		}
		return starts;
	}

}
