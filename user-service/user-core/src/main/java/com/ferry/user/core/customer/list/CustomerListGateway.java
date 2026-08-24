package com.ferry.user.core.customer.list;

import com.ferry.utils.pagination.CursorFetch;
import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerAddressFilter;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerEmailFilter;
import com.ferry.user.domain.customer.CustomerFilter;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
import com.ferry.user.domain.customer.CustomerPhoneFilter;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerListGateway{
	CursorFetch<CustomerDomain> findByFilter(CustomerFilter filter);

	List<CustomerEmailDomain> findEmailsByFilter(CustomerEmailFilter filter);

	List<CustomerPhoneDomain> findPhonesByFilter(CustomerPhoneFilter filter);

	List<CustomerAddressDomain> findAddressesByFilter(CustomerAddressFilter filter);
}
