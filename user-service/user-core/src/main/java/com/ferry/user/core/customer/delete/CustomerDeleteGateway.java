package com.ferry.user.core.customer.delete;

import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerDeleteGateway{
	Optional<Customer> findById(CustomerId customerId, TenantId tenantId);

	Customer save(Customer customer);

	void deleteContacts(String customerId, String updatedBy);
}
