package com.ferry.order.webservice.config;

import com.ferry.promotion.client.PromotionServiceClient;
import com.ferry.user.client.UserServiceClient;
import com.ferry.utils.httpclient.InternalServiceClientConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.HttpServiceGroup;
import org.springframework.web.service.registry.ImportHttpServices;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Configuration
@ImportHttpServices(group = OrderHttpServiceConfig.USER_SERVICE_GROUP, types = UserServiceClient.class,
		clientType = HttpServiceGroup.ClientType.REST_CLIENT)
@ImportHttpServices(group = OrderHttpServiceConfig.PROMOTION_SERVICE_GROUP, types = PromotionServiceClient.class,
		clientType = HttpServiceGroup.ClientType.REST_CLIENT)
public class OrderHttpServiceConfig{

	static final String USER_SERVICE_GROUP = "user-service";
	static final String PROMOTION_SERVICE_GROUP = "promotion-service";

	@Bean
	RestClientHttpServiceGroupConfigurer internalServiceClientConfigurer(){
		return new InternalServiceClientConfigurer(USER_SERVICE_GROUP, PROMOTION_SERVICE_GROUP);
	}

}
