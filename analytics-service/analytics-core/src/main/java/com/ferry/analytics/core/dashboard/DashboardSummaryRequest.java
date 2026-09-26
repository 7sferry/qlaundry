package com.ferry.analytics.core.dashboard;

import com.ferry.analytics.core.tools.AnalyticsValidation;

import java.time.LocalDate;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record DashboardSummaryRequest(LocalDate date) implements AnalyticsValidation{
}
