package com.ferry.user.core.customer.names;

import com.ferry.user.core.customer.constant.CustomerConstant;
import com.ferry.user.core.tools.UserValidation;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.stream.Collectors;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record CustomerNamesRequest(
	@Size(max = CustomerConstant.NAMES_MAX_IDS) Set<String> customerIds) implements UserValidation{

	public Set<String> sanitizedCustomerIds(){
		if(customerIds == null){
			return Set.of();
		}
		return customerIds.stream()
				.filter(id -> id != null && !id.isBlank())
				.map(String::trim)
				.collect(Collectors.toUnmodifiableSet());
	}

}
