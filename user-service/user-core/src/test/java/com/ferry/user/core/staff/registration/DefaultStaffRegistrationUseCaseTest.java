package com.ferry.user.core.staff.registration;

import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.domain.common.HashedPasswordDomain;
import com.ferry.user.domain.common.RawPasswordDomain;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.InvalidPasswordException;
import com.ferry.user.domain.common.exception.InvalidUsernameException;
import com.ferry.user.domain.staff.StaffAddressDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
import com.ferry.user.domain.staff.StaffPhoneDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.token.UserAuthPrincipal;
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
class DefaultStaffRegistrationUseCaseTest{

	private static final String USERNAME = "budirestaff01";
	private static final String PASSWORD = "SecurePass2026";
	private static final String FULL_NAME = "Budi Santoso";
	private static final String EMAIL = "budi@qlaundry.com";
	private static final String PRINCIPAL_ID = "stf-admin-01";
	private static final String TENANT_ID = "tnt-jakarta-01";
	private static final String HASHED_PASSWORD = "hashed-password";
	private static final String GENERATED_ID = "stf-gen-777";

	@Mock
	StaffRegistrationGateway gateway;
	@Mock
	PasswordTool passwordTool;
	@InjectMocks
	DefaultStaffRegistrationUseCase useCase;
	@Mock
	StaffRegistrationPresenter presenter;
	@Captor
	ArgumentCaptor<StaffRegistrationResponse> responseCaptor;
	@Captor
	ArgumentCaptor<StaffDomain> staffCaptor;

	@Test
	void givenBlankUsername_thenThrowsConstraintViolationException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(" ", PASSWORD, FULL_NAME,
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
	void givenUsernameShorterThanMinimumLength_thenThrowsInvalidUsernameException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest("budi", PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidUsernameException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenUsernameAlreadyExists_thenThrowsInvalidUsernameExceptionAndNeverSaves(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(true).given(gateway).existsByUsername(new UsernameDomain(USERNAME));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidUsernameException.class)
				.hasMessage("Username already exists"));

		then(gateway).should(never()).save(any(StaffDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenPasswordShorterThanMinimumLength_thenThrowsInvalidPasswordException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, "short12", FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new UsernameDomain(USERNAME));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidPasswordException.class));

		then(gateway).should(never()).save(any(StaffDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenEmptyEmailsList_thenThrowsIllegalArgumentExceptionAfterStaffIsSaved(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new UsernameDomain(USERNAME));
		willReturn(new HashedPasswordDomain(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPasswordDomain.class));
		willAnswer(invocation -> {
			StaffDomain arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(StaffDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Emails cannot be empty"));

		then(gateway).should().save(any(StaffDomain.class));
		then(gateway).should(never()).save(any(StaffEmailDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNullEmailsList_thenThrowsIllegalArgumentException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, null, null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new UsernameDomain(USERNAME));
		willReturn(new HashedPasswordDomain(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPasswordDomain.class));
		willAnswer(invocation -> {
			StaffDomain arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(StaffDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Emails cannot be empty"));

		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenInvalidEmailFormat_thenThrowsIllegalArgumentException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of("not-an-email"), null, null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new UsernameDomain(USERNAME));
		willReturn(new HashedPasswordDomain(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPasswordDomain.class));
		willAnswer(invocation -> {
			StaffDomain arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(StaffDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid email format."));

		then(gateway).should(never()).save(any(StaffEmailDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenInvalidPhoneFormat_thenThrowsIllegalArgumentExceptionAfterEmailsSaved(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), List.of("not-a-phone"), null);
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new UsernameDomain(USERNAME));
		willReturn(new HashedPasswordDomain(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPasswordDomain.class));
		willAnswer(invocation -> {
			StaffDomain arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(StaffDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Phone must be in E.164 format, e.g. +6281234567890"));

		then(gateway).should().save(any(StaffEmailDomain.class));
		then(gateway).should(never()).save(any(StaffPhoneDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenBlankAddressLine_thenThrowsIllegalArgumentException(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL), null, List.of(" "));
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new UsernameDomain(USERNAME));
		willReturn(new HashedPasswordDomain(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPasswordDomain.class));
		willAnswer(invocation -> {
			StaffDomain arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(StaffDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Address must not be blank"));

		then(gateway).should(never()).save(any(StaffAddressDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenValidRequestWithEmailsPhonesAndAddresses_thenRegistersSuccessfully(){
		StaffRegistrationRequest request = new StaffRegistrationRequest(USERNAME, PASSWORD, FULL_NAME,
				"note", StaffRole.STAFF, List.of(EMAIL, "budi.santoso@gmail.com"),
				List.of("+6281234567890"), List.of("Jl. Sudirman No. 10"));
		UserAuthPrincipal principal = UserAuthPrincipal.builder().userId(PRINCIPAL_ID).tenantId(TENANT_ID)
				.role(StaffRole.SUPER_STAFF).build();
		willReturn(false).given(gateway).existsByUsername(new UsernameDomain(USERNAME));
		willReturn(new HashedPasswordDomain(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPasswordDomain.class));
		willAnswer(invocation -> {
			StaffDomain arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(StaffDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should().save(staffCaptor.capture());
		then(gateway).should(times(2)).save(any(StaffEmailDomain.class));
		then(gateway).should(times(1)).save(any(StaffPhoneDomain.class));
		then(gateway).should(times(1)).save(any(StaffAddressDomain.class));
		then(presenter).should().present(responseCaptor.capture());

		StaffDomain saved = staffCaptor.getValue();
		StaffRegistrationResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
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
		willReturn(false).given(gateway).existsByUsername(new UsernameDomain(USERNAME));
		willReturn(new HashedPasswordDomain(HASHED_PASSWORD)).given(passwordTool).hash(any(RawPasswordDomain.class));
		willAnswer(invocation -> {
			StaffDomain arg = invocation.getArgument(0);
			return arg.toBuilder().id(GENERATED_ID).build();
		}).given(gateway).save(any(StaffDomain.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should().save(any(StaffDomain.class));
		then(gateway).should(times(1)).save(any(StaffEmailDomain.class));
		then(gateway).should(never()).save(any(StaffPhoneDomain.class));
		then(gateway).should(never()).save(any(StaffAddressDomain.class));
		then(presenter).should().present(any(StaffRegistrationResponse.class));
	}

}
