package com.ferry.user.client;

import com.ferry.utils.httpclient.HttpRequestBuilder;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class DefaultUserServiceClient implements UserServiceClient{

	private static final String API_KEY_HEADER = "X-Internal-Api-Key";
	private static final String VERIFICATION_PATH = "/internal/customer/verification";

	private final UserServiceClientConfig config;

	public DefaultUserServiceClient(UserServiceClientConfig config){
		this.config = config;
	}

	@Override
	public boolean verifyCustomer(CustomerVerificationParams params){
		CustomerVerificationClientResponse response = HttpRequestBuilder.get(config.baseUrl() + VERIFICATION_PATH)
				.requestParam(params)
				.header(API_KEY_HEADER, config.apiKey())
				.timeout(config.timeout())
				.build()
				.send(CustomerVerificationClientResponse.class);
		return response != null && response.valid();
	}

}
