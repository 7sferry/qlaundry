package com.ferry.notification.webservice.config;

import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailUseCase;
import com.ferry.notification.core.email.tenantregistration.TenantRegistrationEmailUseCase;
import com.ferry.notification.domain.EmailType;
import com.ferry.notification.webservice.email.forgottenpassword.ForgottenPasswordEmailStreamListener;
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
import org.springframework.data.redis.stream.StreamListener;
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
	                                                                            @Value("${app.notification.stream.email.key}") String streamKeyPrefix,
	                                                                            @Value("${app.notification.stream.email.group}") String group){
		return new TenantRegistrationEmailStreamListener(tenantRegistrationEmailUseCase, jsonManager,
				stringRedisTemplate, streamKeyPrefix + EmailType.TENANT_REGISTRATION.name(), group);
	}

	@Bean
	ForgottenPasswordEmailStreamListener forgottenPasswordEmailStreamListener(ForgottenPasswordEmailUseCase forgottenPasswordEmailUseCase,
	                                                                          JsonManager jsonManager,
	                                                                          StringRedisTemplate stringRedisTemplate,
	                                                                          @Value("${app.notification.stream.email.key}") String streamKeyPrefix,
	                                                                          @Value("${app.notification.stream.email.group}") String group){
		return new ForgottenPasswordEmailStreamListener(forgottenPasswordEmailUseCase, jsonManager,
				stringRedisTemplate, streamKeyPrefix + EmailType.FORGOTTEN_PASSWORD.name(), group);
	}

	@Bean(destroyMethod = "stop")
	StreamMessageListenerContainer<String, MapRecord<String, String, String>> emailListenerContainer(
			RedisConnectionFactory redisConnectionFactory, StringRedisTemplate stringRedisTemplate,
			TenantRegistrationEmailStreamListener tenantRegistrationEmailStreamListener,
			ForgottenPasswordEmailStreamListener forgottenPasswordEmailStreamListener,
			@Value("${app.notification.stream.email.key}") String streamKeyPrefix,
			@Value("${app.notification.stream.email.group}") String group,
			@Value("${app.notification.stream.email.consumer}") String consumer){
		StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
				StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
						.pollTimeout(Duration.ofSeconds(1))
						.build();
		StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
				StreamMessageListenerContainer.create(redisConnectionFactory, options);
		subscribe(container, stringRedisTemplate, streamKeyPrefix + EmailType.TENANT_REGISTRATION.name(), group,
				consumer, tenantRegistrationEmailStreamListener);
		subscribe(container, stringRedisTemplate, streamKeyPrefix + EmailType.FORGOTTEN_PASSWORD.name(), group,
				consumer, forgottenPasswordEmailStreamListener);
		container.start();
		return container;
	}

	private void subscribe(StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
	                       StringRedisTemplate stringRedisTemplate, String streamKey, String group, String consumer,
	                       StreamListener<String, MapRecord<String, String, String>> listener){
		createGroupIfAbsent(stringRedisTemplate, streamKey, group);
		container.receive(Consumer.from(group, consumer), StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
				listener);
	}

	private void createGroupIfAbsent(StringRedisTemplate stringRedisTemplate, String streamKey, String group){
		try{
			stringRedisTemplate.opsForStream().createGroup(streamKey, group);
		}catch(RedisSystemException e){
			// consumer group already exists
		}
	}

}
