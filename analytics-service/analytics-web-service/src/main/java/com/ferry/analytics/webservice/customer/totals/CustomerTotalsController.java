package com.ferry.analytics.webservice.customer.totals;

import com.ferry.analytics.core.customer.totals.CustomerTotalsRequest;
import com.ferry.analytics.core.customer.totals.CustomerTotalsUseCase;
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
public class CustomerTotalsController{
	private final CustomerTotalsUseCase customerTotalsUseCase;

	@GetMapping("/analytics/customer/totals")
	public ResponseEntity<?> getCustomerTotals(CustomerTotalsRequest request,
	                                           @AuthenticationPrincipal AnalyticsAuthPrincipal principal){
		WebCustomerTotalsPresenter presenter = new WebCustomerTotalsPresenter();
		customerTotalsUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
