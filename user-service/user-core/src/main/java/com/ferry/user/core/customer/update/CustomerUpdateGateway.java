package com.ferry.user.core.customer.update;

import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerUpdateGateway{
	Optional<CustomerDomain> findById(CustomerIdDomain customerId, TenantIdDomain tenantId);

	CustomerDomain save(CustomerDomain customer);

	CustomerEmailDomain save(CustomerEmailDomain email);

	CustomerPhoneDomain save(CustomerPhoneDomain phone);

	CustomerAddressDomain save(CustomerAddressDomain address);

	void deleteEmails(String customerId, String updatedBy);

	void deletePhones(String customerId, String updatedBy);

	void deleteAddresses(String customerId, String updatedBy);
}
