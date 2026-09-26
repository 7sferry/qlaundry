package com.ferry.user.core.customer.detail;

import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.CustomerAddressFilter;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerEmailFilter;
import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.customer.CustomerPhone;
import com.ferry.user.domain.customer.CustomerPhoneFilter;
import com.ferry.user.domain.tenant.TenantId;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerDetailGateway{
	Optional<Customer> findById(CustomerId customerId, TenantId tenantId);

	List<CustomerEmail> findEmailsByFilter(CustomerEmailFilter filter);

	List<CustomerPhone> findPhonesByFilter(CustomerPhoneFilter filter);

	List<CustomerAddress> findAddressesByFilter(CustomerAddressFilter filter);
}
