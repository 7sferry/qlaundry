package com.ferry.user.domain.customer;

import com.ferry.user.domain.common.Phone;
import com.ferry.user.domain.common.exception.InvalidUserStateException;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerPhone(
	String id,
	String customerId,
	Phone phone,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public CustomerPhone{
		if(customerId == null || phone == null){
			throw new InvalidUserStateException("Customer id and phone must not be null");
		}
	}

	public static CustomerPhone register(String customerId, Phone phone, String createdBy){
		Instant now = Instant.now();
		return new CustomerPhone(null, customerId, phone, null, false, now, createdBy, now, createdBy);
	}
}
