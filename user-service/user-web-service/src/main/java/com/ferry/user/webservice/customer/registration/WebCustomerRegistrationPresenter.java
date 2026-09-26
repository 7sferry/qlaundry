package com.ferry.user.webservice.customer.registration;

import com.ferry.user.core.customer.registration.CustomerRegistrationPresenter;
import com.ferry.user.core.customer.registration.CustomerRegistrationResponse;
import com.ferry.user.domain.customer.Customer;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebCustomerRegistrationPresenter implements CustomerRegistrationPresenter{
	private ResponseEntity<CustomerRegistrationRestResponse> responseEntity;

	@Override
	public void present(CustomerRegistrationResponse response){
		Customer customer = response.customer();
		String email = response.emails().isEmpty() ? null : response.emails().getFirst().email().value();
		String phone = response.phones().isEmpty() ? null : response.phones().getFirst().phone().value();
		String address = response.addresses().isEmpty()
				? null : response.addresses().getFirst().addressLine().value();
		responseEntity = ResponseEntity.ok(new CustomerRegistrationRestResponse(customer.id(),
				customer.fullNameValue(), phone, email, address, customer.notesValue(),
				customer.createdAt().toEpochMilli()));
	}

}
