package com.ferry.user.core.tenant.registration;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import com.ferry.user.core.tenant.constant.TenantConfirmationConstant;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.core.tools.UserEmailPublisher;
import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.notification.EmailTrigger;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.staff.registration.TurnstileVerificationException;
import com.ferry.user.domain.tenant.Tenant;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
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
	private static final ZoneId TIME_ZONE = ZoneId.of("Asia/Makassar");

	@Mock
	TenantRegistrationGateway gateway;
	@Mock
	UserEmailPublisher emailPublisher;
	@Mock
	VerificationGateway verificationGateway;
	@Mock
	UserCacheManager cacheManager;
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
	@Captor
	ArgumentCaptor<Tenant> tenantCaptor;

	@Test
	void givenBlankFullName_thenThrowsConstraintViolationException(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(" ", TENANT_NAME, "desc", TIME_ZONE,
				USERNAME, PASSWORD, List.of(EMAIL), null, null, CAPTCHA_TOKEN);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(verificationGateway).shouldHaveNoInteractions();
		then(emailPublisher).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenEmptyEmails_thenThrowsConstraintViolationException(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME, "desc", TIME_ZONE,
				USERNAME, PASSWORD, List.of(), null, null, CAPTCHA_TOKEN);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(verificationGateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenCaptchaVerificationFails_thenThrowsTurnstileVerificationException(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME, "desc", TIME_ZONE,
				USERNAME, PASSWORD, List.of(EMAIL), null, null, CAPTCHA_TOKEN);
		willReturn(false).given(verificationGateway).verify(CAPTCHA_TOKEN);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, presenter))
				.isInstanceOf(TurnstileVerificationException.class)
				.hasMessage("Failed captcha verification"));

		then(gateway).shouldHaveNoInteractions();
		then(emailPublisher).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenUsernameAlreadyTaken_thenPresentsFakeResponseAndNeverSavesTenant(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME, "desc", TIME_ZONE,
				USERNAME, PASSWORD, List.of(EMAIL), null, null, CAPTCHA_TOKEN);
		willReturn(true).given(verificationGateway).verify(CAPTCHA_TOKEN);
		willReturn(true).given(gateway).existsByUsername(new Username(USERNAME));

		useCase.execute(request, presenter);

		Tenant fakeTenant = Tenant.fake(new FullName(TENANT_NAME), new Username(USERNAME));
		Staff fakeStaff = Staff.fake(new FullName(FULL_NAME), new Username(USERNAME));
		then(presenter).should().present(new TenantRegistrationResponse(fakeTenant, new StaffRegistrationResponse(fakeStaff)));
		then(gateway).should(never()).save(any(Tenant.class));
		then(gateway).should(never()).registerAdmin(any(StaffRegistrationRequest.class), any(Tenant.class));
		then(emailPublisher).shouldHaveNoInteractions();
	}

	@Test
	void givenValidRequestWithDescription_thenRegistersAdminWithGivenDescription(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME,
				"A great laundry chain", TIME_ZONE, USERNAME, PASSWORD, List.of(EMAIL), null, null,
				CAPTCHA_TOKEN);
		willReturn(true).given(verificationGateway).verify(CAPTCHA_TOKEN);
		willAnswer(invocation -> {
			Tenant arg = invocation.getArgument(0);
			return new Tenant(TENANT_ID, arg.username(), arg.fullName(), arg.description(), arg.timeZone(), arg.status(), null, false,
					arg.createdAt(), arg.createdBy(), arg.updatedAt(), arg.updatedBy());
		}).given(gateway).save(any(Tenant.class));
		Staff admin = Staff.register(new Username(USERNAME),
				new FullName(FULL_NAME), new Description("Super Admin"), TENANT_ID, StaffRole.SUPER_STAFF, null);
		willReturn(new StaffRegistrationResponse(admin)).given(gateway).registerAdmin(staffRequestCaptor.capture(), any(Tenant.class));
		EmailTrigger trigger = EmailTrigger.create(EmailTriggerType.TENANT_REGISTRATION,
				new Email(EMAIL), "{}", null);
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
				null, null, USERNAME, PASSWORD, List.of(EMAIL), null, null,
				CAPTCHA_TOKEN);
		willReturn(true).given(verificationGateway).verify(CAPTCHA_TOKEN);
		willAnswer(invocation -> {
			Tenant arg = invocation.getArgument(0);
			return new Tenant(TENANT_ID, arg.username(), arg.fullName(), arg.description(), arg.timeZone(), arg.status(), null, false,
					arg.createdAt(), arg.createdBy(), arg.updatedAt(), arg.updatedBy());
		}).given(gateway).save(any(Tenant.class));
		Staff admin = Staff.register(new Username(USERNAME),
				new FullName(FULL_NAME), new Description("Super Admin"), TENANT_ID, StaffRole.SUPER_STAFF, null);
		willReturn(new StaffRegistrationResponse(admin)).given(gateway).registerAdmin(staffRequestCaptor.capture(), any(Tenant.class));
		EmailTrigger trigger = EmailTrigger.create(EmailTriggerType.TENANT_REGISTRATION,
				new Email(EMAIL), "{}", null);
		willReturn(trigger).given(emailPublisher).save(any(EmailTriggerConfig.class));

		useCase.execute(request, presenter);

		StaffRegistrationRequest staffRequest = staffRequestCaptor.getValue();
		thenSoftly(softly -> softly.then(staffRequest.description()).isEqualTo("Super Admin"));
	}

	@Test
	void givenValidRequestWithEmails_thenPublishesTenantRegistrationEmailWithCorrectPayload(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME,
				"desc", TIME_ZONE, USERNAME, PASSWORD, List.of(EMAIL), null, null,
				CAPTCHA_TOKEN);
		willReturn(true).given(verificationGateway).verify(CAPTCHA_TOKEN);
		willAnswer(invocation -> {
			Tenant arg = invocation.getArgument(0);
			return new Tenant(TENANT_ID, arg.username(), arg.fullName(), arg.description(), arg.timeZone(), arg.status(), null, false,
					arg.createdAt(), arg.createdBy(), arg.updatedAt(), arg.updatedBy());
		}).given(gateway).save(any(Tenant.class));
		Staff admin = Staff.register(new Username(USERNAME),
				new FullName(FULL_NAME), new Description("Super Admin"), TENANT_ID, StaffRole.SUPER_STAFF, null);
		willReturn(new StaffRegistrationResponse(admin)).given(gateway).registerAdmin(any(StaffRegistrationRequest.class), any(Tenant.class));
		EmailTrigger trigger = EmailTrigger.create(EmailTriggerType.TENANT_REGISTRATION,
				new Email(EMAIL), "{}", null);
		willReturn(trigger).given(emailPublisher).save(emailConfigCaptor.capture());

		useCase.execute(request, presenter);

		then(emailPublisher).should().publish(trigger);
		EmailTriggerConfig config = emailConfigCaptor.getValue();
		TenantRegistrationEmailMessage message = (TenantRegistrationEmailMessage) config.payload();
		then(cacheManager).should().set(eq(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + TENANT_ID),
				eq(message.confirmationToken()), eq(TenantConfirmationConstant.CONFIRM_TOKEN_DURATION));
		thenSoftly(softly -> {
			softly.then(config.triggerType()).isEqualTo(EmailTriggerType.TENANT_REGISTRATION);
			softly.then(config.recipient().value()).isEqualTo(EMAIL);
			softly.then(message.staffFullName()).isEqualTo(FULL_NAME);
			softly.then(message.staffUsername()).isEqualTo(USERNAME);
			softly.then(message.tenantId()).isEqualTo(TENANT_ID);
			softly.then(message.tenantName()).isEqualTo(TENANT_NAME);
			softly.then(message.confirmationToken()).isNotBlank();
		});
	}

	@Test
	void givenValidRequest_thenPresentsResponseWithTenantAndAdmin(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME,
				"desc", TIME_ZONE, USERNAME, PASSWORD, List.of(EMAIL), null, null,
				CAPTCHA_TOKEN);
		willReturn(true).given(verificationGateway).verify(CAPTCHA_TOKEN);
		willAnswer(invocation -> {
			Tenant arg = invocation.getArgument(0);
			return new Tenant(TENANT_ID, arg.username(), arg.fullName(), arg.description(), arg.timeZone(), arg.status(), null, false,
					arg.createdAt(), arg.createdBy(), arg.updatedAt(), arg.updatedBy());
		}).given(gateway).save(any(Tenant.class));
		Staff admin = Staff.register(new Username(USERNAME),
				new FullName(FULL_NAME), new Description("Super Admin"), TENANT_ID, StaffRole.SUPER_STAFF, null);
		willReturn(new StaffRegistrationResponse(admin)).given(gateway).registerAdmin(any(StaffRegistrationRequest.class), any(Tenant.class));
		EmailTrigger trigger = EmailTrigger.create(EmailTriggerType.TENANT_REGISTRATION,
				new Email(EMAIL), "{}", null);
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

	@Test
	void givenBrowserTimeZone_thenSavesTenantWithThatTimeZone(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME,
				"desc", TIME_ZONE, USERNAME, PASSWORD, List.of(EMAIL), null, null,
				CAPTCHA_TOKEN);
		willReturn(true).given(verificationGateway).verify(CAPTCHA_TOKEN);
		willAnswer(invocation -> {
			Tenant arg = invocation.getArgument(0);
			return new Tenant(TENANT_ID, arg.username(), arg.fullName(), arg.description(), arg.timeZone(),
					arg.status(), null, false, arg.createdAt(), arg.createdBy(), arg.updatedAt(), arg.updatedBy());
		}).given(gateway).save(tenantCaptor.capture());
		Staff admin = Staff.register(new Username(USERNAME),
				new FullName(FULL_NAME), new Description("Super Admin"), TENANT_ID, StaffRole.SUPER_STAFF, null);
		willReturn(new StaffRegistrationResponse(admin)).given(gateway).registerAdmin(any(StaffRegistrationRequest.class), any(Tenant.class));
		EmailTrigger trigger = EmailTrigger.create(EmailTriggerType.TENANT_REGISTRATION,
				new Email(EMAIL), "{}", null);
		willReturn(trigger).given(emailPublisher).save(any(EmailTriggerConfig.class));

		useCase.execute(request, presenter);

		thenSoftly(softly -> softly.then(tenantCaptor.getValue().timeZone()).isEqualTo(TIME_ZONE));
	}

	@Test
	void givenNoTimeZone_thenSavesTenantInUtc(){
		TenantRegistrationRequest request = new TenantRegistrationRequest(FULL_NAME, TENANT_NAME,
				"desc", null, USERNAME, PASSWORD, List.of(EMAIL), null, null,
				CAPTCHA_TOKEN);
		willReturn(true).given(verificationGateway).verify(CAPTCHA_TOKEN);
		willAnswer(invocation -> {
			Tenant arg = invocation.getArgument(0);
			return new Tenant(TENANT_ID, arg.username(), arg.fullName(), arg.description(), arg.timeZone(),
					arg.status(), null, false, arg.createdAt(), arg.createdBy(), arg.updatedAt(), arg.updatedBy());
		}).given(gateway).save(tenantCaptor.capture());
		Staff admin = Staff.register(new Username(USERNAME),
				new FullName(FULL_NAME), new Description("Super Admin"), TENANT_ID, StaffRole.SUPER_STAFF, null);
		willReturn(new StaffRegistrationResponse(admin)).given(gateway).registerAdmin(any(StaffRegistrationRequest.class), any(Tenant.class));
		EmailTrigger trigger = EmailTrigger.create(EmailTriggerType.TENANT_REGISTRATION,
				new Email(EMAIL), "{}", null);
		willReturn(trigger).given(emailPublisher).save(any(EmailTriggerConfig.class));

		useCase.execute(request, presenter);

		thenSoftly(softly -> softly.then(tenantCaptor.getValue().timeZone()).isEqualTo(ZoneId.of("UTC")));
	}

}
