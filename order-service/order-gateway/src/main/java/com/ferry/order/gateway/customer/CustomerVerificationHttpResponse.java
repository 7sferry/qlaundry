package com.ferry.order.gateway.customer;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerVerificationHttpResponse(String customerId, String tenantId, boolean valid){
}
