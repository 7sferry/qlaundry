package com.ferry.notification.webservice.config;

import com.ferry.notification.core.email.tenantregistration.TenantRegistrationEmailUseCase;
import com.ferry.notification.webservice.email.tenantregistration.TenantRegistrationEmailStreamListener;
import com.ferry.utils.json.JsonManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Configuration
public class NotificationStreamConfig{

	@Bean
	TenantRegistrationEmailStreamListener tenantRegistrationEmailStreamListener(TenantRegistrationEmailUseCase tenantRegistrationEmailUseCase,
	                                                                            JsonManager jsonManager,
	                                                                            StringRedisTemplate stringRedisTemplate,
	                                                                            @Value("${app.notification.stream.tenant-registration.key}") String streamKey,
	                                                                            @Value("${app.notification.stream.tenant-registration.group}") String group){
		return new TenantRegistrationEmailStreamListener(tenantRegistrationEmailUseCase, jsonManager,
				stringRedisTemplate, streamKey, group);
	}

	@Bean(destroyMethod = "stop")
	StreamMessageListenerContainer<String, MapRecord<String, String, String>> tenantRegistrationEmailListenerContainer(
			RedisConnectionFactory redisConnectionFactory, StringRedisTemplate stringRedisTemplate,
			TenantRegistrationEmailStreamListener tenantRegistrationEmailStreamListener,
			@Value("${app.notification.stream.tenant-registration.key}") String streamKey,
			@Value("${app.notification.stream.tenant-registration.group}") String group,
			@Value("${app.notification.stream.tenant-registration.consumer}") String consumer){
		createGroupIfAbsent(stringRedisTemplate, streamKey, group);
		StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
				StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
						.pollTimeout(Duration.ofSeconds(1))
						.build();
		StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
				StreamMessageListenerContainer.create(redisConnectionFactory, options);
		container.receive(Consumer.from(group, consumer), StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
				tenantRegistrationEmailStreamListener);
		container.start();
		return container;
	}

	private void createGroupIfAbsent(StringRedisTemplate stringRedisTemplate, String streamKey, String group){
		try{
			stringRedisTemplate.opsForStream().createGroup(streamKey, group);
		}catch(RedisSystemException e){
			// consumer group already exists
		}
	}

}
