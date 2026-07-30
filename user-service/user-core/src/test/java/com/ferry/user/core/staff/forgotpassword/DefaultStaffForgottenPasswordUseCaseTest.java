package com.ferry.user.core.staff.forgotpassword;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.staff.constant.PasswordConstant;
import com.ferry.user.core.tenant.registration.UserEmailPublisher;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.notification.EmailTriggerDomain;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.user.domain.staff.forgottenpassword.ForgottenPasswordOtpDomain;
import com.ferry.user.domain.staff.forgottenpassword.StaffEmailForgottenPasswordProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultStaffForgottenPasswordUseCaseTest{

	private static final String USERNAME = "yustinaforgot";
	private static final String EMAIL = "yustina.putri@mailcorporate.com";
	private static final String STAFF_ID = "stf-909";
	private static final String FAKE_EMAIL_PATTERN = "^[a-z]\\*{3,7}[a-z]@[gym]\\*{3,7}\\.com$";

	@Mock
	StaffForgottenPasswordGateway gateway;
	@Mock
	UserEmailPublisher emailPublisher;
	@Mock
	UserCacheManager userCacheManager;
	@InjectMocks
	DefaultStaffForgottenPasswordUseCase useCase;
	@Mock
	StaffForgottenPasswordPresenter presenter;
	@Captor
	ArgumentCaptor<StaffForgottenPasswordResponse> responseCaptor;
	@Captor
	ArgumentCaptor<String> otpCaptor;
	@Captor
	ArgumentCaptor<EmailTriggerConfig> configCaptor;

	@Test
	void givenBlankUsername_thenPresentsMaskedFakeEmailWithoutThrowing(){
		useCase.execute(new StaffForgottenPasswordRequest(" "), presenter);

		then(gateway).shouldHaveNoInteractions();
		then(userCacheManager).shouldHaveNoInteractions();
		then(emailPublisher).shouldHaveNoInteractions();
		then(presenter).should().present(responseCaptor.capture());
		thenSoftly(softly -> softly.then(responseCaptor.getValue().email()).matches(FAKE_EMAIL_PATTERN));
	}

	@Test
	void givenUsernameNotFound_thenPresentsMaskedFakeEmailDeterministically(){
		willReturn(Optional.empty()).given(gateway).findEmailWithUsername(new UsernameDomain(USERNAME));

		useCase.execute(new StaffForgottenPasswordRequest(USERNAME), presenter);

		then(userCacheManager).shouldHaveNoInteractions();
		then(emailPublisher).shouldHaveNoInteractions();
		then(presenter).should().present(responseCaptor.capture());
		String notFoundMaskedEmail = responseCaptor.getValue().email();
		thenSoftly(softly -> softly.then(notFoundMaskedEmail).matches(FAKE_EMAIL_PATTERN));

		reset(gateway, presenter);
		willThrow(new RuntimeException("boom")).given(gateway).findEmailWithUsername(new UsernameDomain(USERNAME));

		useCase.execute(new StaffForgottenPasswordRequest(USERNAME), presenter);

		then(presenter).should().present(responseCaptor.capture());
		String exceptionPathMaskedEmail = responseCaptor.getValue().email();
		thenSoftly(softly -> softly.then(exceptionPathMaskedEmail).isEqualTo(notFoundMaskedEmail));
	}

	@Test
	void givenValidUsernameWithEmailFound_thenGeneratesOtpPublishesEmailAndPresentsMaskedRealEmail(){
		willReturn(Optional.of(new StaffEmailForgottenPasswordProjection(EMAIL, STAFF_ID)))
				.given(gateway).findEmailWithUsername(new UsernameDomain(USERNAME));
		EmailTriggerDomain trigger = EmailTriggerDomain.create(EmailTriggerType.FORGOTTEN_PASSWORD,
				new EmailDomain(EMAIL), "{}", STAFF_ID);
		willReturn(trigger).given(emailPublisher).save(configCaptor.capture());

		useCase.execute(new StaffForgottenPasswordRequest(USERNAME), presenter);

		then(userCacheManager).should().set(eq(PasswordConstant.OTP_KEY + USERNAME), otpCaptor.capture(), eq(PasswordConstant.OTP_DURATION));
		then(emailPublisher).should().publish(trigger);
		then(presenter).should().present(responseCaptor.capture());

		EmailTriggerConfig config = configCaptor.getValue();
		ForgottenPasswordOtpDomain payload = (ForgottenPasswordOtpDomain) config.payload();
		String otp = otpCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(otp).matches("^\\d{6}$");
			softly.then(config.triggerType()).isEqualTo(EmailTriggerType.FORGOTTEN_PASSWORD);
			softly.then(config.userId()).isEqualTo(STAFF_ID);
			softly.then(config.recipient().value()).isEqualTo(EMAIL);
			softly.then(payload.username()).isEqualTo(USERNAME);
			softly.then(payload.otp()).isEqualTo(otp);
			softly.then(responseCaptor.getValue().email()).matches("^y\\*{3,7}i@m\\*{3,7}\\.com$");
		});
	}

	@Test
	void givenGatewayThrowsUnexpectedException_thenPresentsMaskedFakeEmailWithoutPropagating(){
		willThrow(new RuntimeException("boom")).given(gateway).findEmailWithUsername(new UsernameDomain(USERNAME));

		useCase.execute(new StaffForgottenPasswordRequest(USERNAME), presenter);

		then(userCacheManager).shouldHaveNoInteractions();
		then(emailPublisher).shouldHaveNoInteractions();
		then(presenter).should().present(responseCaptor.capture());
		thenSoftly(softly -> softly.then(responseCaptor.getValue().email()).matches(FAKE_EMAIL_PATTERN));
	}

}
