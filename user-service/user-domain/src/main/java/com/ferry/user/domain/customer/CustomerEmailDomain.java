package com.ferry.user.domain.customer;

import com.ferry.user.domain.common.EmailDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerEmailDomain(String id, String customerId, EmailDomain email,
                                  Integer version, boolean deleted, Instant createdAt, String createdBy,
                                  Instant updatedAt, String updatedBy){
	public CustomerEmailDomain{
		if(customerId == null || email == null){
			throw new IllegalArgumentException("Customer id and email must not be null");
		}
	}

	public static CustomerEmailDomain register(String customerId, EmailDomain email, String createdBy){
		Instant now = Instant.now();
		return new CustomerEmailDomain(null, customerId, email, null, false, now, createdBy, now, createdBy);
	}
}
