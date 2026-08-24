package com.ferry.user.domain.customer;

import com.ferry.user.domain.common.AddressLineDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerAddressDomain(String id, String customerId, AddressLineDomain addressLine,
                                    Integer version, boolean deleted, Instant createdAt, String createdBy,
                                    Instant updatedAt, String updatedBy){
	public CustomerAddressDomain{
		if(customerId == null || addressLine == null){
			throw new IllegalArgumentException("Customer id and address must not be null");
		}
	}

	public static CustomerAddressDomain register(String customerId, AddressLineDomain addressLine, String createdBy){
		Instant now = Instant.now();
		return new CustomerAddressDomain(null, customerId, addressLine, null, false, now, createdBy, now, createdBy);
	}
}
