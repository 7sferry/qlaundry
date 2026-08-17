package com.ferry.user.core.staff.update;

import com.ferry.user.core.tools.PasswordTool;
import com.ferry.user.domain.common.AddressLineDomain;
import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.HashedPasswordDomain;
import com.ferry.user.domain.common.PhoneDomain;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.common.exception.InvalidPasswordException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.StaffAddressDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
import com.ferry.user.domain.staff.StaffPasswordDomain;
import com.ferry.user.domain.staff.StaffPasswordProjection;
import com.ferry.user.domain.staff.StaffPhoneDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.token.UserPrincipal;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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
class DefaultStaffUpdateUseCaseTest{

	private static final String USER_ID = "stf-505";
	private static final String FULL_NAME = "Rahmat Hidayat";
	private static final String NEW_DESCRIPTION = "New description";
	private static final String EMAIL = "rahmat@qlaundry.com";
	private static final String USERNAME = "rahmatstaff01";
	private static final String OLD_HASHED_PASSWORD = "old-hash-abc123";
	private static final String TENANT_ID = "tnt-01";
	private static final String PHONE = "+6281234567890";
	private static final String ADDRESS_LINE = "Jl. Merdeka No. 1";

	@Mock
	StaffUpdateGateway gateway;
	@Mock
	PasswordTool passwordTool;
	@InjectMocks
	DefaultStaffUpdateUseCase useCase;
	@Mock
	StaffUpdatePresenter presenter;
	@Captor
	ArgumentCaptor<StaffDomain> staffCaptor;
	@Captor
	ArgumentCaptor<StaffPasswordDomain> passwordCaptor;
	@Captor
	ArgumentCaptor<StaffUpdateResponse> responseCaptor;

	@Test
	void givenBlankFullName_thenThrowsConstraintViolationException(){
		UserPrincipal principal = UserPrincipal.builder().userId(USER_ID).build();
		StaffUpdateRequest request = new StaffUpdateRequest(" ", "desc", null, null, List.of(EMAIL), null, null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenStaffNotFound_thenThrowsNotFoundException(){
		UserPrincipal principal = UserPrincipal.builder().userId(USER_ID).build();
		StaffUpdateRequest request = new StaffUpdateRequest(FULL_NAME, NEW_DESCRIPTION, null, null,
				List.of(EMAIL), null, null);
		willReturn(Optional.empty()).given(gateway).findById(USER_ID);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Staff Not Found"));

		then(gateway).should(never()).save(any(StaffDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenEmptyEmailsList_thenThrowsIllegalArgumentExceptionAfterProfileSaved(){
		UserPrincipal principal = UserPrincipal.builder().userId(USER_ID).build();
		StaffUpdateRequest request = new StaffUpdateRequest(FULL_NAME, NEW_DESCRIPTION, null, null,
				List.of(), null, null);
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME),
				new FullNameDomain("Old Name"), new DescriptionDomain("Old description"), TENANT_ID, StaffRole.STAFF, USER_ID)
				.toBuilder().id(USER_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findById(USER_ID);
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(StaffDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Emails cannot be empty"));

		then(gateway).should().save(any(StaffDomain.class));
		then(gateway).should(never()).deleteEmails(any(), any());
		then(gateway).should(never()).deletePhones(any(), any());
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNullEmailsList_thenThrowsIllegalArgumentException(){
		UserPrincipal principal = UserPrincipal.builder().userId(USER_ID).build();
		StaffUpdateRequest request = new StaffUpdateRequest(FULL_NAME, NEW_DESCRIPTION, null, null,
				null, null, null);
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME),
				new FullNameDomain("Old Name"), new DescriptionDomain("Old description"), TENANT_ID, StaffRole.STAFF, USER_ID)
				.toBuilder().id(USER_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findById(USER_ID);
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(StaffDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Emails cannot be empty"));

		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNewPasswordProvidedButCurrentPasswordMissing_thenThrowsInvalidPasswordException(){
		UserPrincipal principal = UserPrincipal.builder().userId(USER_ID).build();
		StaffUpdateRequest request = new StaffUpdateRequest(FULL_NAME, NEW_DESCRIPTION, null, "NewSecret2026",
				List.of(EMAIL), null, null);
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME),
				new FullNameDomain("Old Name"), new DescriptionDomain("Old description"), TENANT_ID, StaffRole.STAFF, USER_ID)
				.toBuilder().id(USER_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findById(USER_ID);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidPasswordException.class)
				.hasMessage("Current password is incorrect"));

		then(gateway).should(never()).findCurrentPassword(anyString());
		then(gateway).should(never()).save(any(StaffDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNewPasswordProvidedButCurrentPasswordIncorrect_thenThrowsInvalidPasswordException(){
		UserPrincipal principal = UserPrincipal.builder().userId(USER_ID).build();
		StaffUpdateRequest request = new StaffUpdateRequest(FULL_NAME, NEW_DESCRIPTION, "wrong-current-password",
				"NewSecret2026", List.of(EMAIL), null, null);
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME),
				new FullNameDomain("Old Name"), new DescriptionDomain("Old description"), TENANT_ID, StaffRole.STAFF, USER_ID)
				.toBuilder().id(USER_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findById(USER_ID);
		willReturn(Optional.of(new StaffPasswordProjection(OLD_HASHED_PASSWORD)))
				.given(gateway).findCurrentPassword(USER_ID);
		willReturn(false).given(passwordTool).matches("wrong-current-password", OLD_HASHED_PASSWORD);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidPasswordException.class)
				.hasMessage("Current password is incorrect"));

		then(gateway).should(never()).save(any(StaffDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNewPasswordSameAsCurrentPassword_thenThrowsInvalidPasswordException(){
		UserPrincipal principal = UserPrincipal.builder().userId(USER_ID).build();
		StaffUpdateRequest request = new StaffUpdateRequest(FULL_NAME, NEW_DESCRIPTION, "current-password",
				"current-password", List.of(EMAIL), null, null);
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME),
				new FullNameDomain("Old Name"), new DescriptionDomain("Old description"), TENANT_ID, StaffRole.STAFF, USER_ID)
				.toBuilder().id(USER_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findById(USER_ID);
		willReturn(Optional.of(new StaffPasswordProjection(OLD_HASHED_PASSWORD)))
				.given(gateway).findCurrentPassword(USER_ID);
		willReturn(true).given(passwordTool).matches("current-password", OLD_HASHED_PASSWORD);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidPasswordException.class)
				.hasMessage("New password must be different from your current password"));

		then(gateway).should(never()).save(any(StaffDomain.class));
		then(gateway).should(never()).save(any(StaffPasswordDomain.class));
		then(gateway).should(never()).findRecentPasswords(anyString(), any(Instant.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNewPasswordUsedWithinLastThreeMonths_thenThrowsInvalidPasswordException(){
		UserPrincipal principal = UserPrincipal.builder().userId(USER_ID).build();
		StaffUpdateRequest request = new StaffUpdateRequest(FULL_NAME, NEW_DESCRIPTION, "current-password",
				"OldSecret2025", List.of(EMAIL), null, null);
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME),
				new FullNameDomain("Old Name"), new DescriptionDomain("Old description"), TENANT_ID, StaffRole.STAFF, USER_ID)
				.toBuilder().id(USER_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findById(USER_ID);
		willReturn(Optional.of(new StaffPasswordProjection(OLD_HASHED_PASSWORD)))
				.given(gateway).findCurrentPassword(USER_ID);
		willReturn(true).given(passwordTool).matches("current-password", OLD_HASHED_PASSWORD);
		StaffPasswordProjection usedThisQuarter = new StaffPasswordProjection("quarter-old-hash");
		willReturn(List.of(usedThisQuarter)).given(gateway).findRecentPasswords(eq(USER_ID), any(Instant.class));
		willReturn(true).given(passwordTool).matches("OldSecret2025", "quarter-old-hash");

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(InvalidPasswordException.class));

		then(gateway).should(never()).save(any(StaffDomain.class));
		then(gateway).should(never()).save(any(StaffPasswordDomain.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenNoNewPassword_thenNeverTouchesPasswordHistory(){
		UserPrincipal principal = UserPrincipal.builder().userId(USER_ID).build();
		StaffUpdateRequest request = new StaffUpdateRequest(FULL_NAME, NEW_DESCRIPTION, null, null,
				List.of(EMAIL), null, null);
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME),
				new FullNameDomain("Old Name"), new DescriptionDomain("Old description"), TENANT_ID, StaffRole.STAFF, USER_ID)
				.toBuilder().id(USER_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findById(USER_ID);
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(StaffDomain.class));
		willReturn(List.of()).given(gateway).findEmailsByStaffId(USER_ID);
		willReturn(List.of()).given(gateway).findPhonesByStaffId(USER_ID);
		willReturn(List.of()).given(gateway).findAddressesByStaffId(USER_ID);

		useCase.execute(request, principal, presenter);

		then(passwordTool).shouldHaveNoInteractions();
		then(gateway).should(never()).findCurrentPassword(anyString());
		then(gateway).should(never()).save(any(StaffPasswordDomain.class));
		then(gateway).should().save(any(StaffDomain.class));
	}

	@Test
	void givenValidNewPassword_thenHashesAndSavesPasswordHistory(){
		UserPrincipal principal = UserPrincipal.builder().userId(USER_ID).build();
		StaffUpdateRequest request = new StaffUpdateRequest(FULL_NAME, NEW_DESCRIPTION, "current-password",
				"NewSecret2026", List.of(EMAIL), null, null);
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME),
				new FullNameDomain("Old Name"), new DescriptionDomain("Old description"), TENANT_ID, StaffRole.STAFF, USER_ID)
				.toBuilder().id(USER_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findById(USER_ID);
		willReturn(Optional.of(new StaffPasswordProjection(OLD_HASHED_PASSWORD)))
				.given(gateway).findCurrentPassword(USER_ID);
		willReturn(true).given(passwordTool).matches("current-password", OLD_HASHED_PASSWORD);
		willReturn(List.of()).given(gateway).findRecentPasswords(eq(USER_ID), any(Instant.class));
		willReturn(new HashedPasswordDomain("new-hash-xyz789")).given(passwordTool).hash(any());
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(StaffDomain.class));
		willReturn(List.of()).given(gateway).findEmailsByStaffId(USER_ID);
		willReturn(List.of()).given(gateway).findPhonesByStaffId(USER_ID);
		willReturn(List.of()).given(gateway).findAddressesByStaffId(USER_ID);

		useCase.execute(request, principal, presenter);

		then(gateway).should().save(passwordCaptor.capture());
		thenSoftly(softly -> {
			softly.then(passwordCaptor.getValue().passwordValue()).isEqualTo("new-hash-xyz789");
			softly.then(passwordCaptor.getValue().staffId()).isEqualTo(USER_ID);
		});
	}

	@Test
	void givenValidRequest_thenReplacesEmailsPhonesAddressesAndPresentsResponse(){
		UserPrincipal principal = UserPrincipal.builder().userId(USER_ID).build();
		StaffUpdateRequest request = new StaffUpdateRequest(FULL_NAME, NEW_DESCRIPTION, null, null,
				List.of(EMAIL), List.of(PHONE), List.of(ADDRESS_LINE));
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME),
				new FullNameDomain("Old Name"), new DescriptionDomain("Old description"), TENANT_ID, StaffRole.STAFF, USER_ID)
				.toBuilder().id(USER_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findById(USER_ID);
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(StaffDomain.class));
		List<StaffEmailDomain> emails = List.of(StaffEmailDomain.register(USER_ID, new EmailDomain(EMAIL), USER_ID));
		List<StaffPhoneDomain> phones = List.of(StaffPhoneDomain.register(USER_ID, new PhoneDomain(PHONE), USER_ID));
		List<StaffAddressDomain> addresses = List.of(StaffAddressDomain.register(USER_ID, new AddressLineDomain(ADDRESS_LINE), USER_ID));
		willReturn(emails).given(gateway).findEmailsByStaffId(USER_ID);
		willReturn(phones).given(gateway).findPhonesByStaffId(USER_ID);
		willReturn(addresses).given(gateway).findAddressesByStaffId(USER_ID);

		useCase.execute(request, principal, presenter);

		then(gateway).should().save(staffCaptor.capture());
		then(gateway).should().deleteEmails(USER_ID, USER_ID);
		then(gateway).should().deletePhones(USER_ID, USER_ID);
		then(gateway).should().deleteAddresses(USER_ID, USER_ID);
		then(gateway).should(times(1)).save(any(StaffEmailDomain.class));
		then(gateway).should(times(1)).save(any(StaffPhoneDomain.class));
		then(gateway).should(times(1)).save(any(StaffAddressDomain.class));
		then(presenter).should().present(new StaffUpdateResponse(staffCaptor.getValue(), emails, phones, addresses));
	}

	@Test
	void givenEmptyPhonesAndAddresses_thenOnlyDeletesWithoutSavingNewOnes(){
		UserPrincipal principal = UserPrincipal.builder().userId(USER_ID).build();
		StaffUpdateRequest request = new StaffUpdateRequest(FULL_NAME, NEW_DESCRIPTION, null, null,
				List.of(EMAIL), List.of(), List.of());
		StaffDomain existing = StaffDomain.register(new UsernameDomain(USERNAME),
				new FullNameDomain("Old Name"), new DescriptionDomain("Old description"), TENANT_ID, StaffRole.STAFF, USER_ID)
				.toBuilder().id(USER_ID).build();
		willReturn(Optional.of(existing)).given(gateway).findById(USER_ID);
		willAnswer(invocation -> invocation.getArgument(0)).given(gateway).save(any(StaffDomain.class));
		willReturn(List.of()).given(gateway).findEmailsByStaffId(USER_ID);
		willReturn(List.of()).given(gateway).findPhonesByStaffId(USER_ID);
		willReturn(List.of()).given(gateway).findAddressesByStaffId(USER_ID);

		useCase.execute(request, principal, presenter);

		then(gateway).should().deletePhones(USER_ID, USER_ID);
		then(gateway).should().deleteAddresses(USER_ID, USER_ID);
		then(gateway).should(never()).save(any(StaffPhoneDomain.class));
		then(gateway).should(never()).save(any(StaffAddressDomain.class));
	}

}
