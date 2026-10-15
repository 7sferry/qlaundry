package com.ferry.analytics.webservice.customer.summary;

import com.ferry.analytics.core.customer.summary.CustomerSummaryPresenter;
import com.ferry.analytics.core.customer.summary.CustomerSummaryResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebCustomerSummaryPresenter implements CustomerSummaryPresenter{
	private ResponseEntity<CustomerSummaryRestResponse> responseEntity;

	@Override
	public void present(CustomerSummaryResponse response){
		responseEntity = ResponseEntity.ok(new CustomerSummaryRestResponse(response.totalCustomers(),
				response.newCustomers(), response.monthSpend(), response.averageSpend()));
	}

}
