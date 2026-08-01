package com.ferry.user.core.tenant.confirmregistration;

import com.ferry.user.core.tenant.constant.TenantConfirmationConstant;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.domain.tenant.confirmregistration.FailedToConfirmTenantException;
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
 * on Agustus 2026       *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultTenantConfirmRegistrationUseCaseTest{

	private static final String TENANT_ID = "tnt-bandung-07";
	private static final String TOKEN = "aa11bb22cc33";

	@Mock
	TenantConfirmRegistrationGateway gateway;
	@Mock
	UserCacheManager cacheManager;
	@InjectMocks
	DefaultTenantConfirmRegistrationUseCase useCase;
	@Mock
	TenantConfirmRegistrationPresenter presenter;
	@Captor
	ArgumentCaptor<TenantDomain> tenantCaptor;

	@Test
	void givenBlankTenantId_thenThrowsFailedToConfirmTenantExceptionWithConstraintViolationCause(){
		FailedToConfirmTenantException thrown = catchThrowableOfType(FailedToConfirmTenantException.class,
				() -> useCase.execute(new TenantConfirmRegistrationRequest(" ", TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getCause()).isInstanceOf(ConstraintViolationException.class));
		then(gateway).shouldHaveNoInteractions();
		then(cacheManager).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNoConfirmationTokenCached_thenThrowsFailedToConfirmTenantExceptionWithInvalidLinkMessage(){
		willReturn(Optional.empty()).given(cacheManager)
				.get(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + TENANT_ID);

		FailedToConfirmTenantException thrown = catchThrowableOfType(FailedToConfirmTenantException.class,
				() -> useCase.execute(new TenantConfirmRegistrationRequest(TENANT_ID, TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid or expired confirmation link"));
		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenConfirmationTokenMismatch_thenThrowsFailedToConfirmTenantExceptionWithInvalidLinkMessage(){
		willReturn(Optional.of("different-token")).given(cacheManager)
				.get(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + TENANT_ID);

		FailedToConfirmTenantException thrown = catchThrowableOfType(FailedToConfirmTenantException.class,
				() -> useCase.execute(new TenantConfirmRegistrationRequest(TENANT_ID, TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid or expired confirmation link"));
		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenTenantNotFound_thenThrowsFailedToConfirmTenantExceptionWithInvalidLinkMessage(){
		willReturn(Optional.of(TOKEN)).given(cacheManager)
				.get(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + TENANT_ID);
		willReturn(true).given(cacheManager)
				.delete(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + TENANT_ID);
		willReturn(Optional.empty()).given(gateway).findById(new TenantIdDomain(TENANT_ID));

		FailedToConfirmTenantException thrown = catchThrowableOfType(FailedToConfirmTenantException.class,
				() -> useCase.execute(new TenantConfirmRegistrationRequest(TENANT_ID, TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Invalid confirmation link"));
		then(gateway).should(never()).save(any());
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenTenantAlreadyActive_thenThrowsFailedToConfirmTenantExceptionWithAlreadyConfirmedMessage(){
		willReturn(Optional.of(TOKEN)).given(cacheManager)
				.get(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + TENANT_ID);
		willReturn(true).given(cacheManager)
				.delete(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + TENANT_ID);
		TenantDomain tenant = new TenantDomain(TENANT_ID, new FullNameDomain("Bandung Fresh Laundry"),
				new DescriptionDomain("desc"), TenantStatus.ACTIVE, null, false, Instant.now(), null, Instant.now(), null);
		willReturn(Optional.of(tenant)).given(gateway).findById(new TenantIdDomain(TENANT_ID));

		FailedToConfirmTenantException thrown = catchThrowableOfType(FailedToConfirmTenantException.class,
				() -> useCase.execute(new TenantConfirmRegistrationRequest(TENANT_ID, TOKEN), presenter));

		thenSoftly(softly -> softly.then(thrown.getMessage()).isEqualTo("Tenant already confirmed"));
		then(gateway).should(never()).save(any());
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenValidRequest_thenActivatesTenantAndPresentsSuccessMessage(){
		willReturn(Optional.of(TOKEN)).given(cacheManager)
				.get(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + TENANT_ID);
		willReturn(true).given(cacheManager)
				.delete(TenantConfirmationConstant.CONFIRM_TOKEN_KEY + TENANT_ID);
		TenantDomain tenant = new TenantDomain(TENANT_ID, new FullNameDomain("Bandung Fresh Laundry"),
				new DescriptionDomain("desc"), TenantStatus.PENDING, null, false, Instant.now(), null, Instant.now(), null);
		willReturn(Optional.of(tenant)).given(gateway).findById(new TenantIdDomain(TENANT_ID));
		willReturn(tenant.activate()).given(gateway).save(any(TenantDomain.class));

		useCase.execute(new TenantConfirmRegistrationRequest(TENANT_ID, TOKEN), presenter);

		then(gateway).should().save(tenantCaptor.capture());
		then(presenter).should().present(new TenantConfirmRegistrationResponse("Registration confirmed. You can now sign in."));
		thenSoftly(softly -> softly.then(tenantCaptor.getValue().status()).isEqualTo(TenantStatus.ACTIVE));
	}

}
