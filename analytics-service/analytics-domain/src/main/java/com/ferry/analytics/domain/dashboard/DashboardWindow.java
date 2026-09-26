package com.ferry.analytics.domain.dashboard;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record DashboardWindow(
	LocalDate date,
	ZoneId zone){
	public DashboardWindow{
		if(date == null || zone == null){
			throw new InvalidAnalyticStateException("Dashboard date and zone must not be null");
		}
	}

	public LocalDate monthStart(){
		return date.withDayOfMonth(1);
	}

	public Instant dayFrom(){
		return startOf(date);
	}

	public Instant dayTo(){
		return startOf(date.plusDays(1));
	}

	public Instant monthFrom(){
		return startOf(monthStart());
	}

	public Instant monthTo(){
		return startOf(monthStart().plusMonths(1));
	}

	public Instant lastMonthFrom(){
		return startOf(monthStart().minusMonths(1));
	}

	private Instant startOf(LocalDate day){
		return day.atStartOfDay(zone).toInstant();
	}

}
