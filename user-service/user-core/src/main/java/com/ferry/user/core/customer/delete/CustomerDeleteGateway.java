package com.ferry.user.core.customer.delete;

import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerDeleteGateway{
	Optional<CustomerDomain> findById(CustomerIdDomain customerId, TenantIdDomain tenantId);

	CustomerDomain save(CustomerDomain customer);

	void deleteContacts(String customerId, String updatedBy);
}
