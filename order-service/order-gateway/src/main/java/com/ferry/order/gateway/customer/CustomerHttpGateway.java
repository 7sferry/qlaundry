package com.ferry.order.gateway.customer;

import com.ferry.order.core.order.create.CustomerGateway;
import com.ferry.order.core.order.create.CustomerVerificationHttpRequest;
import com.ferry.user.client.CustomerVerificationParams;
import com.ferry.user.client.UserServiceClient;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class CustomerHttpGateway implements CustomerGateway{

	private final UserServiceClient userServiceClient;

	public CustomerHttpGateway(UserServiceClient userServiceClient){
		this.userServiceClient = userServiceClient;
	}

	@Override
	public boolean belongsToTenant(CustomerVerificationHttpRequest request){
		CustomerVerificationParams params = new CustomerVerificationParams(request.customerId(), request.tenantId());
		return userServiceClient.verifyCustomer(params);
	}

}
