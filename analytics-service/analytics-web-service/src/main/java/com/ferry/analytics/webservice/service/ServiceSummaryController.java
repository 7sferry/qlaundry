package com.ferry.analytics.webservice.service;

import com.ferry.analytics.core.service.ServiceSummaryUseCase;
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
public class ServiceSummaryController{
	private final ServiceSummaryUseCase serviceSummaryUseCase;

	@GetMapping("/analytics/service")
	public ResponseEntity<?> getServiceSummary(@AuthenticationPrincipal AnalyticsAuthPrincipal principal){
		WebServiceSummaryPresenter presenter = new WebServiceSummaryPresenter();
		serviceSummaryUseCase.execute(principal, presenter);
		return presenter.getResponseEntity();
	}

}
