package com.ferry.user.core.tools;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.notification.EmailTrigger;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.user.domain.staff.passwordchanged.PasswordChangedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

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
public class DefaultPasswordChangedNotifier implements PasswordChangedNotifier{
	private final UserEmailPublisher emailPublisher;

	@Override
	public void notifyChanged(String staffId, String username, String recipientEmail){
		if(recipientEmail == null || recipientEmail.isBlank()){
			log.warn("Password changed email skipped, staff {} has no email", staffId);
			return;
		}
		try{
			PasswordChangedPayload payload = new PasswordChangedPayload(username, Instant.now());
			EmailTriggerConfig config = new EmailTriggerConfig(payload, staffId,
					EmailTriggerType.PASSWORD_CHANGED, new Email(recipientEmail));
			EmailTrigger saved = emailPublisher.save(config);
			emailPublisher.publish(saved);
		}catch(RuntimeException e){
			log.error("Failed to queue password changed email for staff {}", staffId, e);
		}
	}

}
