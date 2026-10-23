package com.ferry.notification.core.email.passwordchanged;

import com.ferry.notification.core.email.history.EmailHistoryGateway;
import com.ferry.notification.core.email.send.EmailSendGateway;
import com.ferry.notification.domain.Content;
import com.ferry.notification.domain.EmailNotification;
import com.ferry.notification.domain.EmailType;
import com.ferry.notification.domain.exception.InvalidaNotificationStateException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultPasswordChangedEmailUseCaseTest{

	private static final String TRIGGER_ID = "01TRIGGER000000000000000003";
	private static final String RECIPIENT = "wulan@laundrykita.id";
	private static final String USERNAME = "wulanlaundry";
	private static final Instant CHANGED_AT = Instant.parse("2026-10-21T08:15:00Z");

	@Mock
	PasswordChangedEmailComposer composer;
	@Mock
	EmailSendGateway emailSendGateway;
	@Mock
	EmailHistoryGateway emailHistoryGateway;
	@InjectMocks
	DefaultPasswordChangedEmailUseCase useCase;
	@Mock
	PasswordChangedEmailPresenter presenter;
	@Captor
	ArgumentCaptor<EmailNotification> notificationCaptor;
	@Captor
	ArgumentCaptor<Content> contentCaptor;

	@Test
	void givenBlankTriggerId_thenThrowsConstraintViolationException(){
		PasswordChangedEmailRequest request = new PasswordChangedEmailRequest(" ", RECIPIENT, USERNAME, CHANGED_AT);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(composer).shouldHaveNoInteractions();
		then(emailSendGateway).shouldHaveNoInteractions();
		then(emailHistoryGateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNullChangedAt_thenThrowsConstraintViolationException(){
		PasswordChangedEmailRequest request = new PasswordChangedEmailRequest(TRIGGER_ID, RECIPIENT, USERNAME, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(emailSendGateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenInvalidRecipientEmailFormat_thenThrowsInvalidaNotificationStateException(){
		PasswordChangedEmailRequest request = new PasswordChangedEmailRequest(TRIGGER_ID, "wulan-at-nowhere", USERNAME, CHANGED_AT);
		willReturn("<html>changed</html>").given(composer).compose(request);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(InvalidaNotificationStateException.class)
				.hasMessage("Invalid email format."));

		then(emailSendGateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenEmailAlreadySentForTrigger_thenPresentsExistingNotificationWithoutSendingAgain(){
		PasswordChangedEmailRequest request = new PasswordChangedEmailRequest(TRIGGER_ID, RECIPIENT, USERNAME, CHANGED_AT);
		EmailNotification sent = EmailNotification.compose(EmailType.PASSWORD_CHANGED, TRIGGER_ID,
				new com.ferry.notification.domain.Email(RECIPIENT),
				new com.ferry.notification.domain.Subject("QLaundry Password Changed - " + USERNAME)).markSent();
		willReturn(Optional.of(sent)).given(emailHistoryGateway).findByReferenceId(TRIGGER_ID);

		useCase.execute(request, presenter);

		then(presenter).should().present(new PasswordChangedEmailResponse(sent));
		then(composer).shouldHaveNoInteractions();
		then(emailSendGateway).shouldHaveNoInteractions();
		then(emailHistoryGateway).should(never()).save(any(EmailNotification.class));
	}

	@Test
	void givenValidRequest_thenComposesSendsAndSavesEmailHistory(){
		PasswordChangedEmailRequest request = new PasswordChangedEmailRequest(TRIGGER_ID, RECIPIENT, USERNAME, CHANGED_AT);
		willReturn("<html>changed</html>").given(composer).compose(request);
		willAnswer(invocation -> invocation.getArgument(0)).given(emailHistoryGateway).save(any(EmailNotification.class));

		useCase.execute(request, presenter);

		then(emailSendGateway).should().send(notificationCaptor.capture(), contentCaptor.capture());
		then(emailHistoryGateway).should().save(any(EmailNotification.class));
		then(presenter).should().present(any(PasswordChangedEmailResponse.class));

		EmailNotification notification = notificationCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(notification.typeValue()).isEqualTo(EmailType.PASSWORD_CHANGED.name());
			softly.then(notification.referenceId()).isEqualTo(TRIGGER_ID);
			softly.then(notification.recipientValue()).isEqualTo(RECIPIENT);
			softly.then(notification.subjectValue()).isEqualTo("QLaundry Password Changed - " + USERNAME);
			softly.then(contentCaptor.getValue().value()).isEqualTo("<html>changed</html>");
		});
	}

	@Test
	void givenValidRequest_thenSavesNotificationMarkedAsSent(){
		PasswordChangedEmailRequest request = new PasswordChangedEmailRequest(TRIGGER_ID, RECIPIENT, USERNAME, CHANGED_AT);
		willReturn("<html>changed</html>").given(composer).compose(request);
		willAnswer(invocation -> invocation.getArgument(0)).given(emailHistoryGateway).save(notificationCaptor.capture());

		useCase.execute(request, presenter);

		thenSoftly(softly -> softly.then(notificationCaptor.getValue().sentAt()).isNotNull());
	}

}
