package com.ferry.analytics.core.staff;

import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface StaffSummaryUseCase{
	void execute(AnalyticsAuthPrincipal principal, StaffSummaryPresenter presenter);
}
