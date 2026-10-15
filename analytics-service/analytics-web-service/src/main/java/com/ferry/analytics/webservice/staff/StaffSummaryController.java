package com.ferry.analytics.webservice.staff;

import com.ferry.analytics.core.staff.StaffSummaryUseCase;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class StaffSummaryController{
	private final StaffSummaryUseCase staffSummaryUseCase;

	@GetMapping("/analytics/staff")
	public ResponseEntity<?> getStaffSummary(@AuthenticationPrincipal AnalyticsAuthPrincipal principal){
		WebStaffSummaryPresenter presenter = new WebStaffSummaryPresenter();
		staffSummaryUseCase.execute(principal, presenter);
		return presenter.getResponseEntity();
	}

}
