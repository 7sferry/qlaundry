package com.ferry.user.client;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface UserServiceClient{

	String VERIFICATION_PATH = "/internal/customer/verification";

	@GetExchange(VERIFICATION_PATH)
	CustomerVerificationClientResponse verifyCustomer(@RequestParam("customerId") String customerId,
	                                                  @RequestParam("tenantId") String tenantId);

}
