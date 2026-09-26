package com.ferry.user.webservice.customer.delete;

import com.ferry.user.core.customer.delete.CustomerDeletePresenter;
import com.ferry.user.core.customer.delete.CustomerDeleteResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebCustomerDeletePresenter implements CustomerDeletePresenter{
	private ResponseEntity<CustomerDeleteRestResponse> responseEntity;

	@Override
	public void present(CustomerDeleteResponse response){
		responseEntity = ResponseEntity.ok(new CustomerDeleteRestResponse(response.customerId()));
	}

}
