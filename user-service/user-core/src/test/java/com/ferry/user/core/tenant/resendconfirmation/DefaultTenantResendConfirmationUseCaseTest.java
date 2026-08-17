package com.ferry.user.core.tenant.resendconfirmation;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.tenant.constant.TenantConfirmationConstant;
import com.ferry.user.core.tenant.registration.TenantRegistrationEmailMessage;
import com.ferry.user.core.tenant.registration.UserEmailPublisher;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.notification.EmailTriggerDomain;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.domain.tenant.resendconfirmation.FailedToResendConfirmationException;
import com.ferry.user.domain.tenant.resendconfirmation.TenantAdminContactProjection;
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

import static org.assertj.core.api.Assertions.catchThrowableOfType;
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
	void givenBlankTenantId_thenThrowsFailedToResendConfirmationExceptionWithConstraintViolationCause(){
		FailedToResendConfirmationException thrown = catchThrowableOfType(FailedToResendConfirmationException.class,
				() -> useCase.execute(new TenantResendConfirmationRequest(" "), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(ConstraintViolationException.class));
		then(gateway).shouldHaveNoInteractions();
		then(emailPublisher).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenTenantNotFound_thenThrowsFailedToResendConfirmationExceptionWithTenantNotFoundMessage(){
		willReturn(Optional.empty()).given(gateway).findById(new TenantIdDomain(TENANT_ID));

		FailedToResendConfirmationException thrown = catchThrowableOfType(FailedToResendConfirmationException.class,
				() -> useCase.execute(new TenantResendConfirmationRequest(TENANT_ID), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Tenant not found"));
		then(emailPublisher).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenTenantAlreadyActive_thenThrowsFailedToResendConfirmationExceptionWithAlreadyConfirmedMessage(){
		TenantDomain tenant = new TenantDomain(TENANT_ID, new UsernameDomain(TENANT_USERNAME), new FullNameDomain(TENANT_NAME),
				new DescriptionDomain("desc"), TenantStatus.ACTIVE, null, false, Instant.now(), null, Instant.now(), null);
		willReturn(Optional.of(tenant)).given(gateway).findById(new TenantIdDomain(TENANT_ID));

		FailedToResendConfirmationException thrown = catchThrowableOfType(FailedToResendConfirmationException.class,
				() -> useCase.execute(new TenantResendConfirmationRequest(TENANT_ID), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Tenant already confirmed"));
		then(gateway).should(never()).findAdminContact(any());
		then(emailPublisher).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenAdminContactNotFound_thenThrowsFailedToResendConfirmationExceptionWithTenantNotFoundMessage(){
		TenantDomain tenant = new TenantDomain(TENANT_ID, new UsernameDomain(TENANT_USERNAME), new FullNameDomain(TENANT_NAME),
				new DescriptionDomain("desc"), TenantStatus.PENDING, null, false, Instant.now(), null, Instant.now(), null);
		willReturn(Optional.of(tenant)).given(gateway).findById(new TenantIdDomain(TENANT_ID));
		willReturn(Optional.empty()).given(gateway).findAdminContact(new TenantIdDomain(TENANT_ID));

		FailedToResendConfirmationException thrown = catchThrowableOfType(FailedToResendConfirmationException.class,
				() -> useCase.execute(new TenantResendConfirmationRequest(TENANT_ID), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Tenant not found"));
		then(emailPublisher).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenValidRequest_thenCachesNewTokenPublishesEmailAndPresentsSuccessMessage(){
		TenantDomain tenant = new TenantDomain(TENANT_ID, new UsernameDomain(TENANT_USERNAME), new FullNameDomain(TENANT_NAME),
				new DescriptionDomain("desc"), TenantStatus.PENDING, null, false, Instant.now(), null, Instant.now(), null);
		willReturn(Optional.of(tenant)).given(gateway).findById(new TenantIdDomain(TENANT_ID));
		TenantAdminContactProjection admin = new TenantAdminContactProjection(ADMIN_EMAIL, ADMIN_FULL_NAME, ADMIN_USERNAME);
		willReturn(Optional.of(admin)).given(gateway).findAdminContact(new TenantIdDomain(TENANT_ID));
		EmailTriggerDomain trigger = EmailTriggerDomain.create(EmailTriggerType.TENANT_REGISTRATION,
				new EmailDomain(ADMIN_EMAIL), "{}", null);
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
			softly.then(message.recipient()).isEqualTo(ADMIN_EMAIL);
			softly.then(message.staffFullName()).isEqualTo(ADMIN_FULL_NAME);
			softly.then(message.staffUsername()).isEqualTo(ADMIN_USERNAME);
			softly.then(message.tenantId()).isEqualTo(TENANT_ID);
			softly.then(message.tenantName()).isEqualTo(TENANT_NAME);
			softly.then(message.confirmationToken()).isNotBlank();
		});
	}

}
