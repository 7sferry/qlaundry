package com.ferry.analytics.webservice.report;

import com.ferry.analytics.core.report.ReportRequest;
import com.ferry.analytics.core.report.ReportUseCase;
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
public class ReportWebController{
	private final ReportUseCase reportUseCase;

	@GetMapping("/analytics/report")
	public ResponseEntity<?> getReport(ReportRequest request,
	                                   @AuthenticationPrincipal AnalyticsAuthPrincipal principal){
		ReportWebPresenter presenter = new ReportWebPresenter();
		reportUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
