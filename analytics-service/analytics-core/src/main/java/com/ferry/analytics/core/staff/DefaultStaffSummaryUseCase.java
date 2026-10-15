package com.ferry.analytics.core.staff;

import com.ferry.analytics.domain.period.MonthToDateWindow;
import com.ferry.analytics.domain.staff.StaffSummaryProjection;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.time.Clock;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffSummaryUseCase implements StaffSummaryUseCase{
	private final StaffSummaryGateway gateway;
	private final Clock clock;

	@Override
	public void execute(AnalyticsAuthPrincipal principal, StaffSummaryPresenter presenter){
		TenantId tenantId = new TenantId(principal.tenantId());
		MonthToDateWindow window = MonthToDateWindow.endingAt(clock.instant(), principal.timeZone());
		StaffSummaryProjection summary = gateway.summarize(tenantId, window);
		presenter.present(new StaffSummaryResponse(summary.totalStaff(), summary.newStaff()));
	}

}
