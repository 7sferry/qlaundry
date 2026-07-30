package com.ferry.notification.core.email.forgottenpassword;

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
class DefaultForgottenPasswordEmailUseCaseTest{

	public static final String TRIGGER_ID = "01TRIGGER000000000000000002";
	public static final String RECIPIENT = "dadang@qlaundry.com";
	public static final String USERNAME = "dadangsuperstaff";
	public static final String OTP = "123456";

	@Mock
	ForgottenPasswordEmailComposer composer;
	@Mock
	EmailSendGateway emailSendGateway;
	@Mock
	EmailHistoryGateway emailHistoryGateway;
	@InjectMocks
	DefaultForgottenPasswordEmailUseCase useCase;
	@Mock
	ForgottenPasswordEmailPresenter presenter;
	@Captor
	ArgumentCaptor<EmailNotificationDomain> notificationCaptor;
	@Captor
	ArgumentCaptor<ContentDomain> contentCaptor;

	@Test
	void givenBlankTriggerId_thenThrowsConstraintViolationException(){
		ForgottenPasswordEmailRequest request = new ForgottenPasswordEmailRequest(" ", RECIPIENT, USERNAME, OTP);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(composer).shouldHaveNoInteractions();
		then(emailSendGateway).shouldHaveNoInteractions();
		then(emailHistoryGateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenBlankOtp_thenThrowsConstraintViolationException(){
		ForgottenPasswordEmailRequest request = new ForgottenPasswordEmailRequest(TRIGGER_ID, RECIPIENT, USERNAME, " ");

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(emailSendGateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenInvalidRecipientEmailFormat_thenThrowsIllegalArgumentException(){
		ForgottenPasswordEmailRequest request = new ForgottenPasswordEmailRequest(TRIGGER_ID, "not-an-email", USERNAME, OTP);
		willReturn("<html>content</html>").given(composer).compose(request);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid email format."));

		then(emailSendGateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenValidRequest_thenComposesSendsAndSavesEmailHistory(){
		ForgottenPasswordEmailRequest request = new ForgottenPasswordEmailRequest(TRIGGER_ID, RECIPIENT, USERNAME, OTP);
		willReturn("<html>content</html>").given(composer).compose(request);
		willAnswer(invocation -> invocation.getArgument(0)).given(emailHistoryGateway).save(any(EmailNotificationDomain.class));

		useCase.execute(request, presenter);

		then(emailSendGateway).should().send(notificationCaptor.capture(), contentCaptor.capture());
		then(emailHistoryGateway).should().save(any(EmailNotificationDomain.class));
		then(presenter).should().present(any(ForgottenPasswordEmailResponse.class));

		EmailNotificationDomain notification = notificationCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(notification.typeValue()).isEqualTo(EmailType.FORGOTTEN_PASSWORD.name());
			softly.then(notification.referenceId()).isEqualTo(TRIGGER_ID);
			softly.then(notification.recipientValue()).isEqualTo(RECIPIENT);
			softly.then(notification.subjectValue()).isEqualTo("QLaundry Password Reset Verification Code - " + USERNAME);
			softly.then(contentCaptor.getValue().value()).isEqualTo("<html>content</html>");
		});
	}

	@Test
	void givenValidRequest_thenSavesNotificationMarkedAsSent(){
		ForgottenPasswordEmailRequest request = new ForgottenPasswordEmailRequest(TRIGGER_ID, RECIPIENT, USERNAME, OTP);
		willReturn("<html>content</html>").given(composer).compose(request);
		willAnswer(invocation -> invocation.getArgument(0)).given(emailHistoryGateway).save(notificationCaptor.capture());

		useCase.execute(request, presenter);

		thenSoftly(softly -> softly.then(notificationCaptor.getValue().sentAt()).isNotNull());
	}

}
