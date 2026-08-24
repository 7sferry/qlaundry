package com.ferry.user.core.customer.verification;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerVerificationResponse(String customerId, String tenantId, boolean valid){
}
