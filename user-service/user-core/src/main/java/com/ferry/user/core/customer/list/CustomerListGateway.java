package com.ferry.user.core.customer.list;

import com.ferry.utils.pagination.CursorFetch;
import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.CustomerAddressFilter;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerEmailFilter;
import com.ferry.user.domain.customer.CustomerFilter;
import com.ferry.user.domain.customer.CustomerPhone;
import com.ferry.user.domain.customer.CustomerPhoneFilter;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerListGateway{
	CursorFetch<Customer> findByFilter(CustomerFilter filter);

	List<CustomerEmail> findEmailsByFilter(CustomerEmailFilter filter);

	List<CustomerPhone> findPhonesByFilter(CustomerPhoneFilter filter);

	List<CustomerAddress> findAddressesByFilter(CustomerAddressFilter filter);
}
