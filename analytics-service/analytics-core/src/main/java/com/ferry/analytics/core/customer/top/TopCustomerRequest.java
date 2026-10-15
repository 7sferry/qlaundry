package com.ferry.analytics.core.customer.top;

import com.ferry.analytics.core.tools.AnalyticsValidation;
import com.ferry.analytics.domain.report.ReportPeriod;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record TopCustomerRequest(ReportPeriod period) implements AnalyticsValidation{

	public ReportPeriod periodOrDefault(){
		return period == null ? ReportPeriod.MONTH : period;
	}

}
