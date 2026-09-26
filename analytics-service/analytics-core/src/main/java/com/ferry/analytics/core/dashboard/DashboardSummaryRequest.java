package com.ferry.analytics.core.dashboard;

import com.ferry.analytics.core.tools.AnalyticsValidation;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record DashboardSummaryRequest(LocalDate date, String zone) implements AnalyticsValidation{
}
