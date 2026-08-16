package com.ferry.notification.webservice.config;

import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailUseCase;
import com.ferry.notification.core.email.tenantregistration.TenantRegistrationEmailUseCase;
import com.ferry.notification.domain.EmailType;
import com.ferry.notification.webservice.email.forgottenpassword.ForgottenPasswordEmailStreamListener;
import com.ferry.notification.webservice.email.streamtrim.EmailStreamTrimScheduler;
import com.ferry.notification.webservice.email.tenantregistration.TenantRegistrationEmailStreamListener;
import com.ferry.utils.json.JsonManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.ConsumerStreamReadRequest;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamReadRequest;

import java.time.Duration;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Slf4j
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
		List<String> streamKeys = List.of(streamKeyPrefix + EmailType.TENANT_REGISTRATION.name(),
				streamKeyPrefix + EmailType.FORGOTTEN_PASSWORD.name());
		StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
				StreamMessageListenerContainerOptions.builder()
						.pollTimeout(Duration.ofSeconds(1))
						.errorHandler(error -> handlePollError(error, stringRedisTemplate, streamKeys, group))
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
		ConsumerStreamReadRequest<String> readRequest = StreamReadRequest
						.builder(StreamOffset.create(streamKey, ReadOffset.lastConsumed()))
						.consumer(Consumer.from(group, consumer))
						.autoAcknowledge(false)
						.cancelOnError(_ -> false)
						.build();
		container.register(readRequest, listener);
	}

	private void handlePollError(Throwable error, StringRedisTemplate stringRedisTemplate, List<String> streamKeys,
	                             String group){
		if(error.getMessage() != null && error.getMessage().contains("NOGROUP")){
			log.warn("Consumer group missing, recreating: {}", error.getMessage());
			streamKeys.forEach(streamKey -> createGroupIfAbsent(stringRedisTemplate, streamKey, group));
			return;
		}
		log.warn("Stream polling failed, will retry: {}", error.getMessage());
	}

	private void createGroupIfAbsent(StringRedisTemplate stringRedisTemplate, String streamKey, String group){
		try{
			byte[] streamKeyBytes = RedisSerializer.string().serialize(streamKey);
			stringRedisTemplate.execute((RedisCallback<Object>) connection -> connection.streamCommands()
					.xGroupCreate(streamKeyBytes, group, ReadOffset.from("0"), true));
		}catch(RedisSystemException e){
			// consumer group already exists
		}
	}

	@Bean
	EmailStreamTrimScheduler emailStreamTrimScheduler(StringRedisTemplate stringRedisTemplate,
	                                                   @Value("${app.notification.stream.email.key}") String streamKeyPrefix,
	                                                   @Value("${app.notification.stream.email.retention-days}") long retentionDays){
		List<String> streamKeys = List.of(streamKeyPrefix + EmailType.TENANT_REGISTRATION.name(),
				streamKeyPrefix + EmailType.FORGOTTEN_PASSWORD.name());
		EmailStreamTrimScheduler scheduler = new EmailStreamTrimScheduler(stringRedisTemplate, streamKeys, Duration.ofDays(retentionDays));
		Thread.startVirtualThread(scheduler::trimOldEntries);
		return scheduler;
	}

}
