package com.ferry.user.webservice.customer.list;

import com.ferry.user.core.customer.list.CustomerListPresenter;
import com.ferry.user.core.customer.list.CustomerListResponse;
import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerPhone;
import com.ferry.user.webservice.customer.list.CustomerListRestResponse.Customer;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebCustomerListPresenter implements CustomerListPresenter{
	private ResponseEntity<CustomerListRestResponse> responseEntity;

	@Override
	public void present(CustomerListResponse response){
		List<Customer> customers = response.customers().stream()
				.map(o -> {
					List<CustomerEmail> emails = response.emailsByCustomerId()
							.getOrDefault(o.id(), List.of());
					List<CustomerPhone> phones = response.phonesByCustomerId()
							.getOrDefault(o.id(), List.of());
					List<CustomerAddress> addresses = response.addressesByCustomerId()
							.getOrDefault(o.id(), List.of());
					return new Customer(o.id(), o.fullNameValue(),
							phones.isEmpty() ? null : phones.getFirst().phone().value(),
							emails.isEmpty() ? null : emails.getFirst().email().value(),
							addresses.isEmpty() ? null : addresses.getFirst().addressLine().value(),
							o.notesValue(), o.createdAt().toEpochMilli());
				})
				.toList();
		responseEntity = ResponseEntity.ok(new CustomerListRestResponse(customers, response.nextCursor(), response.prevCursor()));
	}

}
