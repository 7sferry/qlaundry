package com.ferry.notification.core.email.tenantregistration;

import com.ferry.notification.core.email.history.EmailHistoryGateway;
import com.ferry.notification.core.email.send.EmailSendGateway;
import com.ferry.notification.domain.ContentDomain;
import com.ferry.notification.domain.EmailNotificationDomain;
import com.ferry.notification.domain.EmailType;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultTenantRegistrationEmailUseCaseTest{

	public static final String TRIGGER_ID = "01TRIGGER000000000000000001";
	public static final String RECIPIENT = "admin@qlaundry.com";
	public static final String STAFF_FULL_NAME = "Dadang Supriatna";
	public static final String STAFF_USERNAME = "dadangsuperstaff";
	public static final String TENANT_NAME = "Q Laundry Bandung";
	public static final String TENANT_ID = "tnt-bandung-11";
	public static final String CONFIRMATION_TOKEN = "conf-token-abc123";

	@Mock
	TenantRegistrationEmailComposer composer;
	@Mock
	EmailSendGateway emailSendGateway;
	@Mock
	EmailHistoryGateway emailHistoryGateway;
	@InjectMocks
	DefaultTenantRegistrationEmailUseCase useCase;
	@Mock
	TenantRegistrationEmailPresenter presenter;
	@Captor
	ArgumentCaptor<EmailNotificationDomain> notificationCaptor;
	@Captor
	ArgumentCaptor<ContentDomain> contentCaptor;

	@Test
	void givenBlankTriggerId_thenThrowsConstraintViolationException(){
		TenantRegistrationEmailRequest request = new TenantRegistrationEmailRequest(" ", RECIPIENT, STAFF_FULL_NAME,
				STAFF_USERNAME, TENANT_ID, TENANT_NAME, "desc", Instant.now(), CONFIRMATION_TOKEN);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(composer).shouldHaveNoInteractions();
		then(emailSendGateway).shouldHaveNoInteractions();
		then(emailHistoryGateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNullRegisteredAt_thenThrowsConstraintViolationException(){
		TenantRegistrationEmailRequest request = new TenantRegistrationEmailRequest(TRIGGER_ID, RECIPIENT, STAFF_FULL_NAME,
				STAFF_USERNAME, TENANT_ID, TENANT_NAME, "desc", null, CONFIRMATION_TOKEN);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(emailSendGateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenInvalidRecipientEmailFormat_thenThrowsIllegalArgumentException(){
		TenantRegistrationEmailRequest request = new TenantRegistrationEmailRequest(TRIGGER_ID, "not-an-email", STAFF_FULL_NAME,
				STAFF_USERNAME, TENANT_ID, TENANT_NAME, "desc", Instant.now(), CONFIRMATION_TOKEN);
		willReturn("<html>content</html>").given(composer).compose(request);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid email format."));

		then(emailSendGateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenValidRequest_thenComposesSendsAndSavesEmailHistory(){
		TenantRegistrationEmailRequest request = new TenantRegistrationEmailRequest(TRIGGER_ID, RECIPIENT, STAFF_FULL_NAME,
				STAFF_USERNAME, TENANT_ID, TENANT_NAME, "A great laundry chain", Instant.now(), CONFIRMATION_TOKEN);
		willReturn("<html>content</html>").given(composer).compose(request);
		willAnswer(invocation -> invocation.getArgument(0)).given(emailHistoryGateway).save(any(EmailNotificationDomain.class));

		useCase.execute(request, presenter);

		then(emailSendGateway).should().send(notificationCaptor.capture(), contentCaptor.capture());
		then(emailHistoryGateway).should().save(any(EmailNotificationDomain.class));
		then(presenter).should().present(any(TenantRegistrationEmailResponse.class));

		EmailNotificationDomain notification = notificationCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(notification.typeValue()).isEqualTo(EmailType.TENANT_REGISTRATION.name());
			softly.then(notification.referenceId()).isEqualTo(TRIGGER_ID);
			softly.then(notification.recipientValue()).isEqualTo(RECIPIENT);
			softly.then(notification.subjectValue()).isEqualTo("Confirm your QLaundry registration - " + TENANT_NAME);
			softly.then(contentCaptor.getValue().value()).isEqualTo("<html>content</html>");
		});
	}

	@Test
	void givenValidRequest_thenSavesNotificationMarkedAsSent(){
		TenantRegistrationEmailRequest request = new TenantRegistrationEmailRequest(TRIGGER_ID, RECIPIENT, STAFF_FULL_NAME,
				STAFF_USERNAME, TENANT_ID, TENANT_NAME, "A great laundry chain", Instant.now(), CONFIRMATION_TOKEN);
		willReturn("<html>content</html>").given(composer).compose(request);
		willAnswer(invocation -> invocation.getArgument(0)).given(emailHistoryGateway).save(notificationCaptor.capture());

		useCase.execute(request, presenter);

		thenSoftly(softly -> softly.then(notificationCaptor.getValue().sentAt()).isNotNull());
	}

}
