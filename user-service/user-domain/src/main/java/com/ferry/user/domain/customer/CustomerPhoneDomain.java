package com.ferry.user.domain.customer;

import com.ferry.user.domain.common.PhoneDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerPhoneDomain(String id, String customerId, PhoneDomain phone,
                                  Integer version, boolean deleted, Instant createdAt, String createdBy,
                                  Instant updatedAt, String updatedBy){
	public CustomerPhoneDomain{
		if(customerId == null || phone == null){
			throw new IllegalArgumentException("Customer id and phone must not be null");
		}
	}

	public static CustomerPhoneDomain register(String customerId, PhoneDomain phone, String createdBy){
		Instant now = Instant.now();
		return new CustomerPhoneDomain(null, customerId, phone, null, false, now, createdBy, now, createdBy);
	}
}
