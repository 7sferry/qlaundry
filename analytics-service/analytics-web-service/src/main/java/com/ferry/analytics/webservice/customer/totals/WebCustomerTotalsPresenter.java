package com.ferry.analytics.webservice.customer.totals;

import com.ferry.analytics.core.customer.totals.CustomerTotalsPresenter;
import com.ferry.analytics.core.customer.totals.CustomerTotalsResponse;
import com.ferry.analytics.webservice.customer.totals.CustomerTotalsRestResponse.CustomerTotals;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebCustomerTotalsPresenter implements CustomerTotalsPresenter{
	private ResponseEntity<CustomerTotalsRestResponse> responseEntity;

	@Override
	public void present(CustomerTotalsResponse response){
		responseEntity = ResponseEntity.ok(new CustomerTotalsRestResponse(response.totals().stream()
				.map(totals -> new CustomerTotals(totals.customerId(), totals.totalOrders(), totals.totalSpend(),
						totals.lastOrderAt().toEpochMilli()))
				.toList()));
	}

}
