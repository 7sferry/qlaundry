package com.ferry.user.webservice.customer.registration;

import com.ferry.user.core.customer.registration.CustomerRegistrationPresenter;
import com.ferry.user.core.customer.registration.CustomerRegistrationResponse;
import com.ferry.user.domain.customer.CustomerDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class CustomerRegistrationWebPresenter implements CustomerRegistrationPresenter{
	private ResponseEntity<CustomerRegistrationWebResponse> responseEntity;

	@Override
	public void present(CustomerRegistrationResponse response){
		CustomerDomain customer = response.customer();
		String email = response.emails().isEmpty() ? null : response.emails().getFirst().email().value();
		String phone = response.phones().isEmpty() ? null : response.phones().getFirst().phone().value();
		String address = response.addresses().isEmpty()
				? null : response.addresses().getFirst().addressLine().value();
		responseEntity = ResponseEntity.ok(new CustomerRegistrationWebResponse(customer.id(),
				customer.fullNameValue(), phone, email, address, customer.notesValue(),
				customer.createdAt().toEpochMilli()));
	}

}
