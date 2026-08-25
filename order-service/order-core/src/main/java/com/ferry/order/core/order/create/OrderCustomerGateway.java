package com.ferry.order.core.order.create;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderCustomerGateway{
	boolean belongsToTenant(CustomerVerificationHttpRequest request);
}
