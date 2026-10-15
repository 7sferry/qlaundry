package com.ferry.analytics.webservice.customer.summary;

import com.ferry.analytics.core.customer.summary.CustomerSummaryRequest;
import com.ferry.analytics.core.customer.summary.CustomerSummaryUseCase;
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
public class CustomerSummaryController{
	private final CustomerSummaryUseCase customerSummaryUseCase;

	@GetMapping("/analytics/customer")
	public ResponseEntity<?> getCustomerSummary(CustomerSummaryRequest request,
	                                            @AuthenticationPrincipal AnalyticsAuthPrincipal principal){
		WebCustomerSummaryPresenter presenter = new WebCustomerSummaryPresenter();
		customerSummaryUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
