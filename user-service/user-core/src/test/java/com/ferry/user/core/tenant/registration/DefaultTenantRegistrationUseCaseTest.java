package com.ferry.user.core.tenant.registration;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.HashedPasswordDomain;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.notification.EmailTriggerDomain;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.staff.registration.TurnstileVerificationException;
import com.ferry.user.domain.tenant.TenantDomain;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
class DefaultTenantRegistrationUseCaseTest{

	private static final String FULL_NAME = "Wahyu Nugroho";
	private static final String TENANT_NAME = "Wahyu Laundry Express";
	private static final String USERNAME = "wahyunugroho1";
	private static final String PASSWORD = "Wahyu@Secure99";
	private static final String EMAIL = "wahyu@qlaundry.com";
	private static final String CAPTCHA_TOKEN = "cf-turnstile-tok-1122";
	private static final String TENANT_ID = "tnt-gen-8899";

	@Mock
	TenantRegistrationGateway gateway;
	@Mock
	UserEmailPublisher emailPublisher;
	@Mock
	TurnstileVerificationGateway turnstileVerificationGateway;
	@InjectMocks
	DefaultTenantRegistrationUseCase useCase;
	@Mock
	TenantRegistrationPresenter presenter;
	@Captor
	ArgumentCaptor<StaffRegistrationRequest> staffRequestCaptor;
	@Captor
	ArgumentCaptor<EmailTriggerConfig> emailConfigCaptor;
	@Captor
	ArgumentCaptor<TenantRegistrationResponse> responseCaptor;

	@Test
	void givenBlankFullName_thenThrowsConstraintViolationException(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(" ", TENANT_NAME, "desc",
				USERNAME, PASSWORD, List.of(EMAIL), null, null, CAPTCHA_TOKEN);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(turnstileVerificationGateway).shouldHaveNoInteractions();
		then(emailPublisher).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenEmptyEmails_thenThrowsConstraintViolationException(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME, "desc",
				USERNAME, PASSWORD, List.of(), null, null, CAPTCHA_TOKEN);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(turnstileVerificationGateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenCaptchaVerificationFails_thenThrowsTurnstileVerificationException(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME, "desc",
				USERNAME, PASSWORD, List.of(EMAIL), null, null, CAPTCHA_TOKEN);
		willReturn(false).given(turnstileVerificationGateway).verify(CAPTCHA_TOKEN);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(TurnstileVerificationException.class)
				.hasMessage("Failed captcha verification"));

		then(gateway).shouldHaveNoInteractions();
		then(emailPublisher).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenValidRequestWithDescription_thenRegistersAdminWithGivenDescription(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME,
				"A great laundry chain", USERNAME, PASSWORD, List.of(EMAIL), null, null,
				CAPTCHA_TOKEN);
		willReturn(true).given(turnstileVerificationGateway).verify(CAPTCHA_TOKEN);
		willAnswer(invocation -> {
			TenantDomain arg = invocation.getArgument(0);
			return new TenantDomain(TENANT_ID, arg.fullName(), arg.description(), null, false,
					arg.createdAt(), arg.createdBy(), arg.updatedAt(), arg.updatedBy());
		}).given(gateway).save(any(TenantDomain.class));
		StaffDomain admin = StaffDomain.register(new UsernameDomain(USERNAME), new HashedPasswordDomain("hashed"),
				new FullNameDomain(FULL_NAME), new DescriptionDomain("Super Admin"), TENANT_ID, StaffRole.SUPER_STAFF, null);
		willReturn(new StaffRegistrationResponse(admin)).given(gateway).registerAdmin(staffRequestCaptor.capture(), any(TenantDomain.class));
		EmailTriggerDomain trigger = EmailTriggerDomain.create(EmailTriggerType.TENANT_REGISTRATION,
				new EmailDomain(EMAIL), "{}", null);
		willReturn(trigger).given(emailPublisher).save(any(EmailTriggerConfig.class));

		useCase.execute(request, presenter);

		StaffRegistrationRequest staffRequest = staffRequestCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(staffRequest.description()).isEqualTo("A great laundry chain");
			softly.then(staffRequest.role()).isEqualTo(StaffRole.SUPER_STAFF);
			softly.then(staffRequest.username()).isEqualTo(USERNAME);
		});
	}

	@Test
	void givenValidRequestWithoutDescription_thenRegistersAdminWithDefaultSuperAdminDescription(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME,
				null, USERNAME, PASSWORD, List.of(EMAIL), null, null,
				CAPTCHA_TOKEN);
		willReturn(true).given(turnstileVerificationGateway).verify(CAPTCHA_TOKEN);
		willAnswer(invocation -> {
			TenantDomain arg = invocation.getArgument(0);
			return new TenantDomain(TENANT_ID, arg.fullName(), arg.description(), null, false,
					arg.createdAt(), arg.createdBy(), arg.updatedAt(), arg.updatedBy());
		}).given(gateway).save(any(TenantDomain.class));
		StaffDomain admin = StaffDomain.register(new UsernameDomain(USERNAME), new HashedPasswordDomain("hashed"),
				new FullNameDomain(FULL_NAME), new DescriptionDomain("Super Admin"), TENANT_ID, StaffRole.SUPER_STAFF, null);
		willReturn(new StaffRegistrationResponse(admin)).given(gateway).registerAdmin(staffRequestCaptor.capture(), any(TenantDomain.class));
		EmailTriggerDomain trigger = EmailTriggerDomain.create(EmailTriggerType.TENANT_REGISTRATION,
				new EmailDomain(EMAIL), "{}", null);
		willReturn(trigger).given(emailPublisher).save(any(EmailTriggerConfig.class));

		useCase.execute(request, presenter);

		StaffRegistrationRequest staffRequest = staffRequestCaptor.getValue();
		thenSoftly(softly -> softly.then(staffRequest.description()).isEqualTo("Super Admin"));
	}

	@Test
	void givenValidRequestWithEmails_thenPublishesTenantRegistrationEmailWithCorrectPayload(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME,
				"desc", USERNAME, PASSWORD, List.of(EMAIL), null, null,
				CAPTCHA_TOKEN);
		willReturn(true).given(turnstileVerificationGateway).verify(CAPTCHA_TOKEN);
		willAnswer(invocation -> {
			TenantDomain arg = invocation.getArgument(0);
			return new TenantDomain(TENANT_ID, arg.fullName(), arg.description(), null, false,
					arg.createdAt(), arg.createdBy(), arg.updatedAt(), arg.updatedBy());
		}).given(gateway).save(any(TenantDomain.class));
		StaffDomain admin = StaffDomain.register(new UsernameDomain(USERNAME), new HashedPasswordDomain("hashed"),
				new FullNameDomain(FULL_NAME), new DescriptionDomain("Super Admin"), TENANT_ID, StaffRole.SUPER_STAFF, null);
		willReturn(new StaffRegistrationResponse(admin)).given(gateway).registerAdmin(any(StaffRegistrationRequest.class), any(TenantDomain.class));
		EmailTriggerDomain trigger = EmailTriggerDomain.create(EmailTriggerType.TENANT_REGISTRATION,
				new EmailDomain(EMAIL), "{}", null);
		willReturn(trigger).given(emailPublisher).save(emailConfigCaptor.capture());

		useCase.execute(request, presenter);

		then(emailPublisher).should().publish(trigger);
		EmailTriggerConfig config = emailConfigCaptor.getValue();
		TenantRegistrationEmailMessage message = (TenantRegistrationEmailMessage) config.payload();
		thenSoftly(softly -> {
			softly.then(config.triggerType()).isEqualTo(EmailTriggerType.TENANT_REGISTRATION);
			softly.then(config.recipient().value()).isEqualTo(EMAIL);
			softly.then(message.recipient()).isEqualTo(EMAIL);
			softly.then(message.staffFullName()).isEqualTo(FULL_NAME);
			softly.then(message.staffUsername()).isEqualTo(USERNAME);
			softly.then(message.tenantId()).isEqualTo(TENANT_ID);
			softly.then(message.tenantName()).isEqualTo(TENANT_NAME);
		});
	}

	@Test
	void givenValidRequest_thenPresentsResponseWithTenantAndAdmin(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME,
				"desc", USERNAME, PASSWORD, List.of(EMAIL), null, null,
				CAPTCHA_TOKEN);
		willReturn(true).given(turnstileVerificationGateway).verify(CAPTCHA_TOKEN);
		willAnswer(invocation -> {
			TenantDomain arg = invocation.getArgument(0);
			return new TenantDomain(TENANT_ID, arg.fullName(), arg.description(), null, false,
					arg.createdAt(), arg.createdBy(), arg.updatedAt(), arg.updatedBy());
		}).given(gateway).save(any(TenantDomain.class));
		StaffDomain admin = StaffDomain.register(new UsernameDomain(USERNAME), new HashedPasswordDomain("hashed"),
				new FullNameDomain(FULL_NAME), new DescriptionDomain("Super Admin"), TENANT_ID, StaffRole.SUPER_STAFF, null);
		willReturn(new StaffRegistrationResponse(admin)).given(gateway).registerAdmin(any(StaffRegistrationRequest.class), any(TenantDomain.class));
		EmailTriggerDomain trigger = EmailTriggerDomain.create(EmailTriggerType.TENANT_REGISTRATION,
				new EmailDomain(EMAIL), "{}", null);
		willReturn(trigger).given(emailPublisher).save(any(EmailTriggerConfig.class));

		useCase.execute(request, presenter);

		then(presenter).should().present(responseCaptor.capture());
		TenantRegistrationResponse response = responseCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(response.tenant().id()).isEqualTo(TENANT_ID);
			softly.then(response.tenantName()).isEqualTo(TENANT_NAME);
			softly.then(response.staffUserName()).isEqualTo(USERNAME);
		});
	}

}
