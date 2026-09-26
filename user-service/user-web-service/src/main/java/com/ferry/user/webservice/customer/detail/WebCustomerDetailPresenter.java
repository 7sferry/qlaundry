package com.ferry.user.webservice.customer.detail;

import com.ferry.user.core.customer.detail.CustomerDetailPresenter;
import com.ferry.user.core.customer.detail.CustomerDetailResponse;
import com.ferry.user.domain.customer.Customer;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebCustomerDetailPresenter implements CustomerDetailPresenter{
	private ResponseEntity<CustomerDetailRestResponse> responseEntity;

	@Override
	public void present(CustomerDetailResponse response){
		Customer customer = response.customer();
		String email = response.emails().isEmpty() ? null : response.emails().getFirst().email().value();
		String phone = response.phones().isEmpty() ? null : response.phones().getFirst().phone().value();
		String address = response.addresses().isEmpty()
				? null : response.addresses().getFirst().addressLine().value();
		responseEntity = ResponseEntity.ok(new CustomerDetailRestResponse(customer.id(), customer.fullNameValue(),
				phone, email, address, customer.notesValue(), customer.createdAt().toEpochMilli()));
	}

}
