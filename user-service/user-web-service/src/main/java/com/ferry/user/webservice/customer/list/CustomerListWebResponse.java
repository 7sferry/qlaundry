package com.ferry.user.webservice.customer.list;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerListWebResponse(List<Customer> customers, String nextCursor, String prevCursor){

	public record Customer(String id, String fullName, String phone, String email, String address, String notes,
	                       long joinedAt){

	}

}
