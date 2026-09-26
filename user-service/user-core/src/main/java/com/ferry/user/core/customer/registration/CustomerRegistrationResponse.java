package com.ferry.user.core.customer.registration;

import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerPhone;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerRegistrationResponse(
	Customer customer,
	List<CustomerEmail> emails,
	List<CustomerPhone> phones,
	List<CustomerAddress> addresses){
}
