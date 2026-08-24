package com.ferry.user.webservice.customer.list;

import com.ferry.user.core.customer.list.CustomerListPresenter;
import com.ferry.user.core.customer.list.CustomerListResponse;
import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
import com.ferry.user.webservice.customer.list.CustomerListWebResponse.Customer;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class CustomerListWebPresenter implements CustomerListPresenter{
	private ResponseEntity<CustomerListWebResponse> responseEntity;

	@Override
	public void present(CustomerListResponse response){
		List<Customer> customers = response.customers().stream()
				.map(o -> {
					List<CustomerEmailDomain> emails = response.emailsByCustomerId()
							.getOrDefault(o.id(), List.of());
					List<CustomerPhoneDomain> phones = response.phonesByCustomerId()
							.getOrDefault(o.id(), List.of());
					List<CustomerAddressDomain> addresses = response.addressesByCustomerId()
							.getOrDefault(o.id(), List.of());
					return new Customer(o.id(), o.fullNameValue(),
							phones.isEmpty() ? null : phones.getFirst().phone().value(),
							emails.isEmpty() ? null : emails.getFirst().email().value(),
							addresses.isEmpty() ? null : addresses.getFirst().addressLine().value(),
							o.notesValue(), o.createdAt().toEpochMilli());
				})
				.toList();
		responseEntity = ResponseEntity.ok(new CustomerListWebResponse(customers));
	}

}
