package com.ferry.user.webservice.customer.update;

import com.ferry.user.core.customer.update.CustomerUpdatePresenter;
import com.ferry.user.core.customer.update.CustomerUpdateResponse;
import com.ferry.user.domain.customer.Customer;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebCustomerUpdatePresenter implements CustomerUpdatePresenter{
	private ResponseEntity<CustomerUpdateRestResponse> responseEntity;

	@Override
	public void present(CustomerUpdateResponse response){
		Customer customer = response.customer();
		String email = response.emails().isEmpty() ? null : response.emails().getFirst().email().value();
		String phone = response.phones().isEmpty() ? null : response.phones().getFirst().phone().value();
		String address = response.addresses().isEmpty()
				? null : response.addresses().getFirst().addressLine().value();
		responseEntity = ResponseEntity.ok(new CustomerUpdateRestResponse(customer.id(), customer.fullNameValue(),
				phone, email, address, customer.notesValue(), customer.createdAt().toEpochMilli()));
	}

}
