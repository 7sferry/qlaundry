package com.ferry.notification.webservice.email.forgottenpassword;

import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailRequest;
import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailUseCase;
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
public class ForgottenPasswordEmailStreamListener implements StreamListener<String, MapRecord<String, String, String>>{
	private static final String TRIGGER_ID_FIELD = "triggerId";
	private static final String RECIPIENT_FIELD = "recipient";
	private static final String PAYLOAD_FIELD = "payload";

	private final ForgottenPasswordEmailUseCase forgottenPasswordEmailUseCase;
	private final JsonManager jsonManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final String streamKey;
	private final String group;

	@Override
	public void onMessage(@NonNull MapRecord<String, String, String> record){
		try{
			Map<String, String> value = record.getValue();
			ForgottenPasswordEmailMessage message = jsonManager.readValue(value.get(PAYLOAD_FIELD), ForgottenPasswordEmailMessage.class);
			ForgottenPasswordEmailRequest request = new ForgottenPasswordEmailRequest(value.get(TRIGGER_ID_FIELD),
					value.get(RECIPIENT_FIELD), message.username(), message.otp());
			ForgottenPasswordEmailStreamPresenter presenter = new ForgottenPasswordEmailStreamPresenter();
			forgottenPasswordEmailUseCase.execute(request, presenter);
			stringRedisTemplate.opsForStream().acknowledge(streamKey, group, record.getId());
			log.info("Forgotten password email sent to {} for record {}",
					presenter.getNotification().recipientValue(), record.getId());
		}catch(RuntimeException e){
			// not acknowledged; the record stays pending on the consumer group for reprocessing
			log.error("Failed to process forgotten password email for record {}", record.getId(), e);
		}
	}

}
