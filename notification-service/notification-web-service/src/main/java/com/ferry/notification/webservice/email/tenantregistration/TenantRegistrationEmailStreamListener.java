package com.ferry.notification.webservice.email.tenantregistration;

import com.ferry.notification.core.email.tenantregistration.TenantRegistrationEmailRequest;
import com.ferry.notification.core.email.tenantregistration.TenantRegistrationEmailUseCase;
import com.ferry.utils.json.JsonManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class TenantRegistrationEmailStreamListener implements StreamListener<String, MapRecord<String, String, String>>{
	private static final String TRIGGER_ID_FIELD = "triggerId";
	private static final String PAYLOAD_FIELD = "payload";

	private final TenantRegistrationEmailUseCase tenantRegistrationEmailUseCase;
	private final JsonManager jsonManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final String streamKey;
	private final String group;

	@Override
	public void onMessage(@NonNull MapRecord<String, String, String> record){
		try{
			Map<String, String> value = record.getValue();
			TenantRegistrationEmailMessage message = jsonManager.readValue(value.get(PAYLOAD_FIELD), TenantRegistrationEmailMessage.class);
			TenantRegistrationEmailRequest request = new TenantRegistrationEmailRequest(value.get(TRIGGER_ID_FIELD),
					message.recipient(), message.staffFullName(), message.staffUsername(), message.tenantId(),
					message.tenantName(), message.tenantDescription(), message.registeredAt(),
					message.confirmationToken());
			TenantRegistrationEmailStreamPresenter presenter = new TenantRegistrationEmailStreamPresenter();
			tenantRegistrationEmailUseCase.execute(request, presenter);
			stringRedisTemplate.opsForStream().acknowledge(streamKey, group, record.getId());
			log.info("Tenant registration email sent to {} for record {}",
					presenter.getNotification().recipientValue(), record.getId());
		}catch(RuntimeException e){
			// not acknowledged; the record stays pending on the consumer group for reprocessing
			log.error("Failed to process tenant registration email for record {}", record.getId(), e);
		}
	}

}
