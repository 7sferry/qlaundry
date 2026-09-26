package com.ferry.user.domain.customer;

import com.ferry.user.domain.common.AddressLine;
import com.ferry.user.domain.common.exception.InvalidUserStateException;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerAddress(
	String id,
	String customerId,
	AddressLine addressLine,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public CustomerAddress{
		if(customerId == null || addressLine == null){
			throw new InvalidUserStateException("Customer id and address must not be null");
		}
	}

	public static CustomerAddress register(String customerId, AddressLine addressLine, String createdBy){
		Instant now = Instant.now();
		return new CustomerAddress(null, customerId, addressLine, null, false, now, createdBy, now, createdBy);
	}
}
