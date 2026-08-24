package com.ferry.user.webservice.customer.update;

import com.ferry.user.core.customer.update.CustomerUpdatePresenter;
import com.ferry.user.core.customer.update.CustomerUpdateResponse;
import com.ferry.user.domain.customer.CustomerDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class CustomerUpdateWebPresenter implements CustomerUpdatePresenter{
	private ResponseEntity<CustomerUpdateWebResponse> responseEntity;

	@Override
	public void present(CustomerUpdateResponse response){
		CustomerDomain customer = response.customer();
		String email = response.emails().isEmpty() ? null : response.emails().getFirst().email().value();
		String phone = response.phones().isEmpty() ? null : response.phones().getFirst().phone().value();
		String address = response.addresses().isEmpty()
				? null : response.addresses().getFirst().addressLine().value();
		responseEntity = ResponseEntity.ok(new CustomerUpdateWebResponse(customer.id(), customer.fullNameValue(),
				phone, email, address, customer.notesValue(), customer.createdAt().toEpochMilli()));
	}

}
