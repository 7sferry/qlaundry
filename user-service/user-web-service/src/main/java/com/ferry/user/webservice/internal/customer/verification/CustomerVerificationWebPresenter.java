package com.ferry.user.webservice.internal.customer.verification;

import com.ferry.user.core.customer.verification.CustomerVerificationPresenter;
import com.ferry.user.core.customer.verification.CustomerVerificationResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class CustomerVerificationWebPresenter implements CustomerVerificationPresenter{
	private ResponseEntity<CustomerVerificationWebResponse> responseEntity;

	@Override
	public void present(CustomerVerificationResponse response){
		responseEntity = ResponseEntity.ok(new CustomerVerificationWebResponse(response.customerId(),
				response.tenantId(), response.valid()));
	}

}
