package com.ferry.user.core.customer.registration;

import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerPhone;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerRegistrationGateway{
	Customer save(Customer register);

	CustomerEmail save(CustomerEmail register);

	CustomerPhone save(CustomerPhone register);

	CustomerAddress save(CustomerAddress register);
}
