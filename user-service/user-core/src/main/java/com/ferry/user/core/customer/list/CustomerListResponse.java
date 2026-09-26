package com.ferry.user.core.customer.list;

import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerPhone;

import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerListResponse(
	List<Customer> customers,
	Map<String, List<CustomerEmail>> emailsByCustomerId,
	Map<String, List<CustomerPhone>> phonesByCustomerId,
	Map<String, List<CustomerAddress>> addressesByCustomerId,
	String nextCursor,
	String prevCursor){
}
