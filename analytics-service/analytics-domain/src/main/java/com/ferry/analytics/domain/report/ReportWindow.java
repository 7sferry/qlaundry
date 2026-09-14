package com.ferry.analytics.domain.report;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record ReportWindow(ReportPeriod period, LocalDate from, LocalDate toExclusive){
	public ReportWindow{
		if(period == null || from == null || toExclusive == null){
			throw new IllegalArgumentException("Report period and bounds must not be null");
		}
		if(!from.isBefore(toExclusive)){
			throw new IllegalArgumentException("Report start must be before its end");
		}
	}

	public ReportBucket bucket(){
		return period.getBucket();
	}

	public List<LocalDate> bucketStarts(){
		List<LocalDate> starts = new ArrayList<>();
		for(LocalDate start = from; start.isBefore(toExclusive); start = bucket().next(start)){
			starts.add(start);
		}
		return starts;
	}

}
