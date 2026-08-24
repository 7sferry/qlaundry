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
public class CustomerDeleteWebPresenter implements CustomerDeletePresenter{
	private ResponseEntity<CustomerDeleteWebResponse> responseEntity;

	@Override
	public void present(CustomerDeleteResponse response){
		responseEntity = ResponseEntity.ok(new CustomerDeleteWebResponse(response.customerId()));
	}

}
