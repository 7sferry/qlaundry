package com.ferry.user.webservice.internal.customer.verification;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerVerificationRestResponse(
	String customerId,
	String tenantId,
	boolean valid){
}
