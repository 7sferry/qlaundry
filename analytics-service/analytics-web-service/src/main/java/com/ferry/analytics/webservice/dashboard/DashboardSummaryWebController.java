package com.ferry.analytics.webservice.dashboard;

import com.ferry.analytics.core.dashboard.DashboardSummaryRequest;
import com.ferry.analytics.core.dashboard.DashboardSummaryUseCase;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RestController
@RequiredArgsConstructor
public class DashboardSummaryWebController{
	private final DashboardSummaryUseCase dashboardSummaryUseCase;

	@GetMapping("/analytics/dashboard")
	public ResponseEntity<?> getDashboard(@AuthenticationPrincipal AnalyticsAuthPrincipal principal){
		DashboardSummaryWebPresenter presenter = new DashboardSummaryWebPresenter();
		dashboardSummaryUseCase.execute(new DashboardSummaryRequest(), principal, presenter);
		return presenter.getResponseEntity();
	}

}
