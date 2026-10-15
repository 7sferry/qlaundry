package com.ferry.analytics.webservice.customer.top;

import com.ferry.analytics.core.customer.top.TopCustomerUseCase;
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
public class TopCustomerController{
	private final TopCustomerUseCase topCustomerUseCase;

	@GetMapping("/analytics/customer/top")
	public ResponseEntity<?> getTopCustomers(@AuthenticationPrincipal AnalyticsAuthPrincipal principal){
		WebTopCustomerPresenter presenter = new WebTopCustomerPresenter();
		topCustomerUseCase.execute(principal, presenter);
		return presenter.getResponseEntity();
	}

}
