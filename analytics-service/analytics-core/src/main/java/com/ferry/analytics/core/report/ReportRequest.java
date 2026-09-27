package com.ferry.analytics.core.report;

import com.ferry.analytics.core.tools.AnalyticsValidation;
import com.ferry.analytics.domain.report.ReportPeriod;
import jakarta.validation.constraints.NotNull;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record ReportRequest(@NotNull ReportPeriod period) implements AnalyticsValidation{
}
