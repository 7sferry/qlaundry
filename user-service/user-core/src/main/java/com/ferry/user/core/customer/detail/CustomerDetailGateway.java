package com.ferry.user.core.customer.detail;

import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerAddressFilter;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerEmailFilter;
import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
import com.ferry.user.domain.customer.CustomerPhoneFilter;
import com.ferry.user.domain.tenant.TenantIdDomain;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerDetailGateway{
	Optional<CustomerDomain> findById(CustomerIdDomain customerId, TenantIdDomain tenantId);

	List<CustomerEmailDomain> findEmailsByFilter(CustomerEmailFilter filter);

	List<CustomerPhoneDomain> findPhonesByFilter(CustomerPhoneFilter filter);

	List<CustomerAddressDomain> findAddressesByFilter(CustomerAddressFilter filter);
}
