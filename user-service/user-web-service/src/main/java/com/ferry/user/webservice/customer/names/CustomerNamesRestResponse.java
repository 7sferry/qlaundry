package com.ferry.user.webservice.customer.names;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record CustomerNamesRestResponse(List<CustomerName> customers){

	public record CustomerName(
		String customerId,
		String fullName){
	}

}
