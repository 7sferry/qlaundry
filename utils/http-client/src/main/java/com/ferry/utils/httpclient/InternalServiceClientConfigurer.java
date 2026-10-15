package com.ferry.utils.httpclient;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public class InternalServiceClientConfigurer implements RestClientHttpServiceGroupConfigurer{

	private final String[] groupNames;

	public InternalServiceClientConfigurer(String... groupNames){
		this.groupNames = groupNames;
	}

	@Override
	public void configureGroups(Groups<RestClient.Builder> groups){
		groups.filterByName(groupNames)
				.forEachClient((_, builder) -> builder.requestInterceptor(new InternalServiceClientInterceptor()));
	}

}
