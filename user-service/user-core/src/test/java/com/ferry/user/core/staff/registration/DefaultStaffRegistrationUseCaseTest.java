package com.ferry.user.core.staff.registration;

import com.ferry.user.core.audit.AuthAuditLog;
import com.ferry.user.core.audit.AuthAction;
import com.ferry.user.core.audit.AuthResult;
import com.ferry.user.core.tools.PasswordSubject;
import com.ferry.user.core.tools.PasswordSubjectGateway;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.common.exception.PasswordPolicyException;
import com.ferry.user.core.analytics.UserAnalyticsEvent;
import com.ferry.user.core.analytics.StaffAnalyticsMessage;
import com.ferry.user.core.analytics.UserAnalyticsPublisher;
import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.domain.analytics.UserAnalyticsEventType;
import com.ferry.user.domain.common.HashedPassword;
import com.ferry.user.domain.common.RawPassword;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.InvalidPasswordException;
import com.ferry.user.domain.common.exception.InvalidUserStateException;
import com.ferry.user.domain.common.exception.InvalidUsernameException;
import com.ferry.user.domain.staff.StaffAddress;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffEmail;
import com.ferry.user.domain.staff.StaffPhone;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.token.UserAuthPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
class DefaultStaffRegistrationUseCaseTest{

	private static final String USERNAME = "budirestaff01";
	private static final String PASSWORD = "SecurePass2026";
	private static final String FULL_NAME = "Budi Santoso";
	private static final String EMAIL = "budi@qlaundry.com";
	private static final String PRINCIPAL_ID = "stf-admin-01";
	private static final String TENANT_ID = "tnt-jakarta-01";
	private static final String HASHED_PASSWORD = "hashed-password";
	private static final String GENERATED_ID = "stf-gen-777";
	private static final PasswordSubject TENANT_SUBJECT = new PasswordSubject(null, null, "Laundry Kilat Jakarta",
			"qlk_cabang07", null, null);

	@Mock
	StaffRegistrationGateway gateway;
	@Mock
	PasswordTool passwordTool;
	@Mock
	UserAnalyticsPublisher analyticsPublisher;
	@Mock
	PasswordSubjectGateway subjectGateway;
	@Mock
	AuthAuditLog auditLog;
	@InjectMocks
	DefaultStaffRegistrationUseCase useCase;
	@Mock
	StaffRegistrationPresenter presenter;
	@Captor
	ArgumentCaptor<StaffRegistrationResponse> responseCaptor;
	@Captor
	ArgumentCaptor<Staff> staffCaptor;
	@Captor
	ArgumentCaptor<UserAnalyticsEvent> analyticsCaptor;

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"   ", "budi", "budi@01"})
	void givenInvalidUsername_thenThrowsConstraintViolationException(String username){
		StaffRegistrationRequest request = new StaffRegistrationRequest(username, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenBlankPassword_thenThrowsConstraintViolationException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, " ", FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenBlankFullName_thenThrowsConstraintViolationException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, " ",
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNullRole_thenThrowsConstraintViolationException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", null, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNonSuperStaffRolePrincipal_thenThrowsConstraintViolationException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.STAFF).build();

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ForbiddenActionException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenUsernameAlreadyExists_thenThrowsInvalidUsernameExceptionAndNeverSaves(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(true).given(gateway).existsByUsername(new Username(USERNAME));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidUsernameException.class)
				.hasMessage("Username already exists"));

		then(gateway).should(never()).save(any(Staff.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenPasswordShorterThanMinimumLength_thenThrowsInvalidPasswordException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, "short12", FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new Username(USERNAME));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidPasswordException.class));

		then(gateway).should(never()).save(any(Staff.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenPasswordContainsUsername_thenThrowsPasswordPolicyExceptionAndNeverSaves(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, "xx-BudiReStaff01-xx", FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new Username(USERNAME));
		willReturn(Optional.of(TENANT_SUBJECT)).given(subjectGateway).findByTenantId(TENANT_ID);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(PasswordPolicyException.class)
				.hasMessage("Password must not contain your username"));

		then(gateway).should(never()).save(any(Staff.class));
		then(passwordTool).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
		then(auditLog).should().record(AuthAction.STAFF_REGISTRATION, AuthResult.FAILURE, PRINCIPAL_ID, USERNAME);
	}

	@Test
	void givenPasswordContainsLocalPartOfNewStaffEmail_thenThrowsPasswordPolicyException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, "Pw-Kasir.Depan-2026", FULL_NAME,
				"note", StaffRole.STAFF, List.of("kasir.depan@mailku.id"), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new Username(USERNAME));
		willReturn(Optional.of(TENANT_SUBJECT)).given(subjectGateway).findByTenantId(TENANT_ID);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(PasswordPolicyException.class)
				.hasMessage("Password must not contain your email address"));

		then(gateway).should(never()).save(any(Staff.class));
	}

	@Test
	void givenPasswordContainsTenantUsername_thenThrowsPasswordPolicyException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, "Xx-QLK_Cabang07-Zz", FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new Username(USERNAME));
		willReturn(Optional.of(TENANT_SUBJECT)).given(subjectGateway).findByTenantId(TENANT_ID);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(PasswordPolicyException.class)
				.hasMessage("Password must not contain the tenant username"));

		then(gateway).should(never()).save(any(Staff.class));
	}

	@Test
	void givenTenantOfPrincipalNotFound_thenThrowsNotFoundExceptionAndNeverSaves(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new Username(USERNAME));
		willReturn(Optional.empty()).given(subjectGateway).findByTenantId(TENANT_ID);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Tenant Not Found"));

		then(gateway).should(never()).save(any(Staff.class));
	}

	@Test
	void givenEmptyEmailsList_thenThrowsInvalidUserStateExceptionAfterStaffIsSaved(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new Username(USERNAME));
		willReturn(Optional.of(TENANT_SUBJECT)).given(subjectGateway).findByTenantId(TENANT_ID);
		willReturn(new HashedPassword(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPassword.class));
		willAnswer(invocation -> {
			Staff arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(Staff.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidUserStateException.class)
				.hasMessage("Emails cannot be empty"));

		then(gateway).should().save(any(Staff.class));
		then(gateway).should(never()).save(any(StaffEmail.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNullEmailsList_thenThrowsInvalidUserStateException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, null, null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new Username(USERNAME));
		willReturn(Optional.of(TENANT_SUBJECT)).given(subjectGateway).findByTenantId(TENANT_ID);
		willReturn(new HashedPassword(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPassword.class));
		willAnswer(invocation -> {
			Staff arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(Staff.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidUserStateException.class)
				.hasMessage("Emails cannot be empty"));

		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenInvalidEmailFormat_thenThrowsInvalidUserStateException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of("not-an-email"), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new Username(USERNAME));
		willReturn(Optional.of(TENANT_SUBJECT)).given(subjectGateway).findByTenantId(TENANT_ID);
		willReturn(new HashedPassword(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPassword.class));
		willAnswer(invocation -> {
			Staff arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(Staff.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidUserStateException.class)
				.hasMessage("Invalid email format."));

		then(gateway).should(never()).save(any(StaffEmail.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenInvalidPhoneFormat_thenThrowsInvalidUserStateExceptionAfterEmailsSaved(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), List.of("not-a-phone"), null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new Username(USERNAME));
		willReturn(Optional.of(TENANT_SUBJECT)).given(subjectGateway).findByTenantId(TENANT_ID);
		willReturn(new HashedPassword(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPassword.class));
		willAnswer(invocation -> {
			Staff arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(Staff.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidUserStateException.class)
				.hasMessage("Phone must be in E.164 format, e.g. +6281234567890"));

		then(gateway).should().save(any(StaffEmail.class));
		then(gateway).should(never()).save(any(StaffPhone.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenBlankAddressLine_thenThrowsInvalidUserStateException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, List.of(" "));
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new Username(USERNAME));
		willReturn(Optional.of(TENANT_SUBJECT)).given(subjectGateway).findByTenantId(TENANT_ID);
		willReturn(new HashedPassword(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPassword.class));
		willAnswer(invocation -> {
			Staff arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(Staff.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidUserStateException.class)
				.hasMessage("Address must not be blank"));

		then(gateway).should(never()).save(any(StaffAddress.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenValidRequestWithEmailsPhonesAndAddresses_thenRegistersSuccessfully(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL, "budi.santoso@gmail.com"),
				List.of("+6281234567890"), List.of("Jl. Sudirman No. 10"));
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new Username(USERNAME));
		willReturn(Optional.of(TENANT_SUBJECT)).given(subjectGateway).findByTenantId(TENANT_ID);
		willReturn(new HashedPassword(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPassword.class));
		willAnswer(invocation -> {
			Staff arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(Staff.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should().save(staffCaptor.capture());
		then(gateway).should(times(2)).save(any(StaffEmail.class));
		then(gateway).should(times(1)).save(any(StaffPhone.class));
		then(gateway).should(times(1)).save(any(StaffAddress.class));
		then(analyticsPublisher).should().save(analyticsCaptor.capture());
		then(presenter).should().present(responseCaptor.capture());

		Staff saved = staffCaptor.getValue();
		StaffRegistrationResponse response = responseCaptor.getValue();
		UserAnalyticsEvent analytics = analyticsCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(analytics.type()).isEqualTo(UserAnalyticsEventType.STAFF_REGISTERED);
			softly.then(analytics.aggregateId()).isEqualTo(GENERATED_ID);
			softly.then(analytics.tenantId()).isEqualTo(TENANT_ID);
			softly.then(((StaffAnalyticsMessage) analytics.payload()).role()).isEqualTo(StaffRole.STAFF);
			softly.then(saved.id()).isNull();
			softly.then(saved.usernameValue()).isEqualTo(USERNAME);
			softly.then(saved.tenantId()).isEqualTo(TENANT_ID);
			softly.then(response.user().id()).isEqualTo(GENERATED_ID);
		});
	}

	@Test
	void givenValidRequestWithOnlyMandatoryEmails_thenRegistersSuccessfullyWithoutPhoneOrAddress(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new Username(USERNAME));
		willReturn(Optional.of(TENANT_SUBJECT)).given(subjectGateway).findByTenantId(TENANT_ID);
		willReturn(new HashedPassword(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPassword.class));
		willAnswer(invocation -> {
			Staff arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(Staff.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should().save(any(Staff.class));
		then(gateway).should(times(1)).save(any(StaffEmail.class));
		then(gateway).should(never()).save(any(StaffPhone.class));
		then(gateway).should(never()).save(any(StaffAddress.class));
		then(presenter).should().present(any(StaffRegistrationResponse.class));
	}

}
