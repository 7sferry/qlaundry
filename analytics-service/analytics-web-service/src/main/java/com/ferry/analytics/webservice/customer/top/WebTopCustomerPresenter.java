package com.ferry.analytics.webservice.customer.top;

import com.ferry.analytics.core.customer.top.TopCustomerPresenter;
import com.ferry.analytics.core.customer.top.TopCustomerResponse;
import com.ferry.analytics.webservice.customer.top.TopCustomerRestResponse.TopCustomer;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebTopCustomerPresenter implements TopCustomerPresenter{
	private ResponseEntity<TopCustomerRestResponse> responseEntity;

	@Override
	public void present(TopCustomerResponse response){
		responseEntity = ResponseEntity.ok(new TopCustomerRestResponse(response.customers().stream()
				.map(customer -> new TopCustomer(customer.customerId(), customer.totalOrders(),
						customer.totalSpend()))
				.toList()));
	}

}
