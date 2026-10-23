package com.ferry.user.core.tools;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.notification.EmailTrigger;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.user.domain.staff.passwordchanged.PasswordChangedPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPasswordChangedNotifierTest{

	private static final String STAFF_ID = "stf-yuni-31";
	private static final String USERNAME = "yunilaundry";
	private static final String EMAIL = "yuni@cucibersih.id";

	@Mock
	UserEmailPublisher emailPublisher;
	@InjectMocks
	DefaultPasswordChangedNotifier notifier;
	@Captor
	ArgumentCaptor<EmailTriggerConfig> configCaptor;

	@Test
	void givenRecipient_thenSavesAndPublishesPasswordChangedTrigger(){
		EmailTrigger trigger = mock(EmailTrigger.class);
		willReturn(trigger).given(emailPublisher).save(any(EmailTriggerConfig.class));

		notifier.notifyChanged(STAFF_ID, USERNAME, EMAIL);

		then(emailPublisher).should().save(configCaptor.capture());
		then(emailPublisher).should().publish(trigger);
		EmailTriggerConfig config = configCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(config.triggerType()).isEqualTo(EmailTriggerType.PASSWORD_CHANGED);
			softly.then(config.userId()).isEqualTo(STAFF_ID);
			softly.then(config.recipient()).isEqualTo(new Email(EMAIL));
			softly.then(config.payload()).isInstanceOf(PasswordChangedPayload.class);
			softly.then(((PasswordChangedPayload) config.payload()).username()).isEqualTo(USERNAME);
			softly.then(((PasswordChangedPayload) config.payload()).changedAt()).isNotNull();
		});
	}

	@Test
	void givenBlankRecipient_thenSkipsWithoutPublishing(){
		notifier.notifyChanged(STAFF_ID, USERNAME, "  ");

		then(emailPublisher).shouldHaveNoInteractions();
	}

	@Test
	void givenPublisherFails_thenSwallowsTheFailure(){
		given(emailPublisher.save(any(EmailTriggerConfig.class))).willThrow(new IllegalStateException("db down"));

		notifier.notifyChanged(STAFF_ID, USERNAME, EMAIL);

		then(emailPublisher).should().save(any(EmailTriggerConfig.class));
		then(emailPublisher).shouldHaveNoMoreInteractions();
	}

}
