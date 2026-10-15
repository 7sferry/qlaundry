package com.ferry.user.webservice.customer.names;

import com.ferry.user.core.customer.names.CustomerNamesPresenter;
import com.ferry.user.core.customer.names.CustomerNamesResponse;
import com.ferry.user.webservice.customer.names.CustomerNamesRestResponse.CustomerName;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebCustomerNamesPresenter implements CustomerNamesPresenter{
	private ResponseEntity<CustomerNamesRestResponse> responseEntity;

	@Override
	public void present(CustomerNamesResponse response){
		responseEntity = ResponseEntity.ok(new CustomerNamesRestResponse(response.customers().stream()
				.map(customer -> new CustomerName(customer.customerId(), customer.fullName()))
				.toList()));
	}

}
