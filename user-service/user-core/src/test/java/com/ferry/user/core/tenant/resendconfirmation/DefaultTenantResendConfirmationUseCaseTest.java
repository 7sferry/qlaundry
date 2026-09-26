package com.ferry.user.core.tenant.resendconfirmation;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.tenant.constant.TenantConfirmationConstant;
import com.ferry.user.core.tenant.registration.TenantRegistrationEmailMessage;
import com.ferry.user.core.tools.UserEmailPublisher;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.notification.EmailTrigger;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.domain.tenant.resendconfirmation.TenantAdminContactProjection;
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
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultTenantResendConfirmationUseCaseTest{

	private static final String TENANT_ID = "tnt-semarang-03";
	private static final String TENANT_USERNAME = "semarangkilat03";
	private static final String TENANT_NAME = "Semarang Kilat Laundry";
	private static final String ADMIN_EMAIL = "putri@qlaundry.com";
	private static final String ADMIN_FULL_NAME = "Putri Handayani";
	private static final String ADMIN_USERNAME = "putrisuperstaff";
	private static final String ADMIN_STAFF_ID = "stf-707";

	@Mock
	TenantResendConfirmationGateway gateway;
	@Mock
	UserEmailPublisher emailPublisher;
	@Mock
	UserCacheManager cacheManager;
	@InjectMocks
	DefaultTenantResendConfirmationUseCase useCase;
	@Mock
	TenantResendConfirmationPresenter presenter;
	@Captor
	ArgumentCaptor<EmailTriggerConfig> emailConfigCaptor;

	@Test
	void givenBlankTenantId_thenPresentsGenericSuccessMessageWithoutSendingEmail(){
		useCase.execute(new TenantResendConfirmationRequest(" "), presenter);

		then(presenter).should().present(new TenantResendConfirmationResponse("A new confirmation email has been sent."));
		then(gateway).shouldHaveNoInteractions();
		then(emailPublisher).shouldHaveNoInteractions();
	}

	@Test
	void givenTenantNotFound_thenPresentsGenericSuccessMessageWithoutSendingEmail(){
		willReturn(Optional.empty()).given(gateway).findById(new TenantId(TENANT_ID));

		useCase.execute(new TenantResendConfirmationRequest(TENANT_ID), presenter);

		then(presenter).should().present(new TenantResendConfirmationResponse("A new confirmation email has been sent."));
		then(emailPublisher).shouldHaveNoInteractions();
	}

	@Test
	void givenTenantAlreadyActive_thenPresentsGenericSuccessMessageWithoutSendingEmail(){
		Tenant tenant = new Tenant(TENANT_ID, new Username(TENANT_USERNAME), new FullName(TENANT_NAME),
				new Description("desc"), TenantStatus.ACTIVE, null, false, Instant.now(), null, Instant.now(), null);
		willReturn(Optional.of(tenant)).given(gateway).findById(new TenantId(TENANT_ID));

		useCase.execute(new TenantResendConfirmationRequest(TENANT_ID), presenter);

		then(presenter).should().present(new TenantResendConfirmationResponse("A new confirmation email has been sent."));
		then(gateway).should(never()).findAdminContact(any());
		then(emailPublisher).shouldHaveNoInteractions();
	}

	@Test
	void givenAdminContactNotFound_thenPresentsGenericSuccessMessageWithoutSendingEmail(){
		Tenant tenant = new Tenant(TENANT_ID, new Username(TENANT_USERNAME), new FullName(TENANT_NAME),
				new Description("desc"), TenantStatus.PENDING, null, false, Instant.now(), null, Instant.now(), null);
		willReturn(Optional.of(tenant)).given(gateway).findById(new TenantId(TENANT_ID));
		willReturn(Optional.empty()).given(gateway).findAdminContact(new TenantId(TENANT_ID));

		useCase.execute(new TenantResendConfirmationRequest(TENANT_ID), presenter);

		then(presenter).should().present(new TenantResendConfirmationResponse("A new confirmation email has been sent."));
		then(emailPublisher).shouldHaveNoInteractions();
	}

	@Test
	void givenValidRequest_thenCachesNewTokenPublishesEmailAndPresentsSuccessMessage(){
		Tenant tenant = new Tenant(TENANT_ID, new Username(TENANT_USERNAME), new FullName(TENANT_NAME),
				new Description("desc"), TenantStatus.PENDING, null, false, Instant.now(), null, Instant.now(), null);
		willReturn(Optional.of(tenant)).given(gateway).findById(new TenantId(TENANT_ID));
		TenantAdminContactProjection admin = new TenantAdminContactProjection(ADMIN_EMAIL, ADMIN_FULL_NAME, ADMIN_USERNAME, ADMIN_STAFF_ID);
		willReturn(Optional.of(admin)).given(gateway).findAdminContact(new TenantId(TENANT_ID));
		EmailTrigger trigger = EmailTrigger.create(EmailTriggerType.TENANT_REGISTRATION,
				new Email(ADMIN_EMAIL), "{}", null);
		willReturn(trigger).given(emailPublisher).save(emailConfigCaptor.capture());

		useCase.execute(new TenantResendConfirmationRequest(TENANT_ID), presenter);

		then(emailPublisher).should().publish(trigger);
		then(presenter).should().present(new TenantResendConfirmationResponse("A new confirmation email has been sent."));
		EmailTriggerConfig config = emailConfigCaptor.getValue();
		TenantRegistrationEmailMessage message = (TenantRegistrationEmailMessage) config.payload();
		then(cacheManager).should().set(eq(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + TENANT_ID),
				eq(message.confirmationToken()), eq(TenantConfirmationConstant.CONFIRM_TOKEN_DURATION));
		thenSoftly(softly -> {
			softly.then(config.triggerType()).isEqualTo(EmailTriggerType.TENANT_REGISTRATION);
			softly.then(message.staffFullName()).isEqualTo(ADMIN_FULL_NAME);
			softly.then(message.staffUsername()).isEqualTo(ADMIN_USERNAME);
			softly.then(message.tenantId()).isEqualTo(TENANT_ID);
			softly.then(message.tenantName()).isEqualTo(TENANT_NAME);
			softly.then(message.confirmationToken()).isNotBlank();
		});
	}

}
