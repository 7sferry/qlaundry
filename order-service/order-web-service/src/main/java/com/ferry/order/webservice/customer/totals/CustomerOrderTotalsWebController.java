package com.ferry.order.webservice.customer.totals;

import com.ferry.order.core.customer.totals.CustomerOrderTotalsRequest;
import com.ferry.order.core.customer.totals.CustomerOrderTotalsUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RestController
@RequiredArgsConstructor
public class CustomerOrderTotalsWebController{
	private final CustomerOrderTotalsUseCase customerOrderTotalsUseCase;

	@Transactional(readOnly = true)
	@GetMapping("/order/customer-totals")
	public ResponseEntity<?> getTotals(CustomerOrderTotalsRequest request,
	                                   @AuthenticationPrincipal OrderAuthPrincipal principal){
		CustomerOrderTotalsWebPresenter presenter = new CustomerOrderTotalsWebPresenter();
		customerOrderTotalsUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
