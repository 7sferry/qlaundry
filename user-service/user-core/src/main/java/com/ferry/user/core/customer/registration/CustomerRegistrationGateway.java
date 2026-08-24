package com.ferry.user.core.customer.registration;

import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerRegistrationGateway{
	CustomerDomain save(CustomerDomain register);

	CustomerEmailDomain save(CustomerEmailDomain register);

	CustomerPhoneDomain save(CustomerPhoneDomain register);

	CustomerAddressDomain save(CustomerAddressDomain register);
}
