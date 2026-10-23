package com.ferry.notification.webservice.email.passwordchanged;

import com.ferry.notification.core.email.passwordchanged.PasswordChangedEmailRequest;
import com.ferry.notification.core.email.passwordchanged.PasswordChangedEmailUseCase;
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
 * on Oktober 2026      *
 ************************/

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class PasswordChangedEmailStreamListener implements StreamListener<String, MapRecord<String, String, String>>{
	private static final String TRIGGER_ID_FIELD = "triggerId";
	private static final String RECIPIENT_FIELD = "recipient";
	private static final String PAYLOAD_FIELD = "payload";

	private final PasswordChangedEmailUseCase passwordChangedEmailUseCase;
	private final JsonManager jsonManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final String streamKey;
	private final String group;

	@Override
	public void onMessage(@NonNull MapRecord<String, String, String> record){
		try{
			Map<String, String> value = record.getValue();
			PasswordChangedEmailMessage message = jsonManager.readValue(value.get(PAYLOAD_FIELD), PasswordChangedEmailMessage.class);
			PasswordChangedEmailRequest request = new PasswordChangedEmailRequest(value.get(TRIGGER_ID_FIELD),
					value.get(RECIPIENT_FIELD), message.username(), message.changedAt());
			StreamPasswordChangedEmailPresenter presenter = new StreamPasswordChangedEmailPresenter();
			passwordChangedEmailUseCase.execute(request, presenter);
			stringRedisTemplate.opsForStream().acknowledge(streamKey, group, record.getId());
			log.info("Password changed email sent for trigger {} and record {}",
					value.get(TRIGGER_ID_FIELD), record.getId());
		}catch(RuntimeException e){
			log.error("Failed to process password changed email for record {}", record.getId(), e);
		}
	}

}
