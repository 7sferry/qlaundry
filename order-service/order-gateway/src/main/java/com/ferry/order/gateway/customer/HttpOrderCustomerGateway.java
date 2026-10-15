package com.ferry.order.gateway.customer;

import com.ferry.order.core.order.create.OrderCustomerGateway;
import com.ferry.order.core.order.create.CustomerVerificationHttpRequest;
import com.ferry.user.client.CustomerVerificationClientResponse;
import com.ferry.user.client.UserServiceClient;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class HttpOrderCustomerGateway implements OrderCustomerGateway{

	private final UserServiceClient userServiceClient;

	public HttpOrderCustomerGateway(UserServiceClient userServiceClient){
		this.userServiceClient = userServiceClient;
	}

	@Override
	public boolean belongsToTenant(CustomerVerificationHttpRequest request){
		CustomerVerificationClientResponse response = userServiceClient.verifyCustomer(request.customerId(),
				request.tenantId());
		return response != null && response.valid();
	}

}
