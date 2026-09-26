package com.ferry.user.core.customer.update;

import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.customer.CustomerPhone;
import com.ferry.user.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerUpdateGateway{
	Optional<Customer> findById(CustomerId customerId, TenantId tenantId);

	Customer save(Customer customer);

	CustomerEmail save(CustomerEmail email);

	CustomerPhone save(CustomerPhone phone);

	CustomerAddress save(CustomerAddress address);

	void deleteEmails(String customerId, String updatedBy);

	void deletePhones(String customerId, String updatedBy);

	void deleteAddresses(String customerId, String updatedBy);
}
