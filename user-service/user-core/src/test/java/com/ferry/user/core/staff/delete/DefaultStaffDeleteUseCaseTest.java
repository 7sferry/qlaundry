package com.ferry.user.core.staff.delete;

import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.InvalidUsernameException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.token.UserAuthPrincipal;
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

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultStaffDeleteUseCaseTest{

	public static final String USERNAME = "dadang";
	public static final String TENANT_ID = "tenant-01";
	public static final String TARGET_ID = "01ARGET0000000000000000000";
	public static final String PRINCIPAL_ID = "01PRINCIPAL0000000000000000";

	@Mock
	StaffDeleteGateway gateway;
	@InjectMocks
	DefaultStaffDeleteUseCase useCase;
	@Mock
	StaffDeletePresenter presenter;
	@Captor
	ArgumentCaptor<StaffDeleteResponse> responseCaptor;
	@Captor
	ArgumentCaptor<StaffDomain> staffCaptor;

	private UserAuthPrincipal superStaffPrincipal(String tenantId){
		return UserAuthPrincipal.builder()
				.userId(PRINCIPAL_ID)
				.tenantId(tenantId)
				.role(StaffRole.SUPER_STAFF)
				.build();
	}

	private StaffDomain staff(String id){
		Instant now = Instant.now();
		return StaffDomain.builder()
				.id(id)
				.username(new UsernameDomain(USERNAME))
				.fullName(new FullNameDomain("Full Name"))
				.description(new DescriptionDomain("desc"))
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.deleted(false)
				.createdAt(now)
				.createdBy(PRINCIPAL_ID)
				.updatedAt(now)
				.updatedBy(PRINCIPAL_ID)
				.build();
	}

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder()
				.role(StaffRole.STAFF)
				.build();

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new StaffDeleteRequest(USERNAME), principal, presenter))
				.isInstanceOf(ForbiddenActionException.class)
				.hasMessage("Only super staff can delete staff"));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(StaffDeleteResponse.class));
	}

	@Test
	void givenBlankUsername_thenThrowsConstraintViolationException(){
		UserAuthPrincipal principal = superStaffPrincipal(TENANT_ID);

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new StaffDeleteRequest(" "), principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(StaffDeleteResponse.class));
	}

	@Test
	void givenUsernameShorterThanMinimumLength_thenThrowsInvalidUsernameException(){
		UserAuthPrincipal principal = superStaffPrincipal(TENANT_ID);

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new StaffDeleteRequest("ab"), principal, presenter))
				.isInstanceOf(InvalidUsernameException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(StaffDeleteResponse.class));
	}

	@Test
	void givenPrincipalWithoutTenantId_thenThrowsIllegalArgumentException(){
		UserAuthPrincipal principal = superStaffPrincipal(null);

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new StaffDeleteRequest(USERNAME), principal, presenter))
				.isInstanceOf(IllegalArgumentException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(StaffDeleteResponse.class));
	}

	@Test
	void givenStaffNotFound_thenThrowsNotFoundException(){
		UserAuthPrincipal principal = superStaffPrincipal(TENANT_ID);
		willReturn(Optional.empty()).given(gateway)
				.findByUsername(any(UsernameDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new StaffDeleteRequest(USERNAME), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Staff Not Found"));

		then(gateway).should(never())
				.save(any(StaffDomain.class));
		then(presenter).should(never())
				.present(any(StaffDeleteResponse.class));
	}

	@Test
	void givenTargetIsSameAsPrincipal_thenThrowsForbiddenActionException(){
		UserAuthPrincipal principal = superStaffPrincipal(TENANT_ID);
		StaffDomain self = staff(PRINCIPAL_ID);
		willReturn(Optional.of(self)).given(gateway)
				.findByUsername(any(UsernameDomain.class), any(TenantIdDomain.class));

		thenSoftly(softly -> softly.thenThrownBy(() ->
						useCase.execute(new StaffDeleteRequest(USERNAME), principal, presenter))
				.isInstanceOf(ForbiddenActionException.class)
				.hasMessage("Cannot delete your own account"));

		then(gateway).should(never())
				.save(any(StaffDomain.class));
		then(presenter).should(never())
				.present(any(StaffDeleteResponse.class));
	}

	@Test
	void givenValidSuperStaffDeletingAnotherStaff_thenDeletesSuccessfully(){
		UserAuthPrincipal principal = superStaffPrincipal(TENANT_ID);
		StaffDomain target = staff(TARGET_ID);
		willReturn(Optional.of(target)).given(gateway)
				.findByUsername(any(UsernameDomain.class), any(TenantIdDomain.class));

		useCase.execute(new StaffDeleteRequest(USERNAME), principal, presenter);

		then(gateway).should()
				.findByUsername(eq(new UsernameDomain(USERNAME)), eq(new TenantIdDomain(TENANT_ID)));
		then(gateway).should()
				.save(staffCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		StaffDomain saved = staffCaptor.getValue();
		StaffDeleteResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.deleted()).isTrue();
			softly.then(saved.id()).isEqualTo(TARGET_ID);
			softly.then(saved.updatedBy()).isEqualTo(PRINCIPAL_ID);
			softly.then(response.username()).isEqualTo(USERNAME);
		});
	}

	@Test
	void givenUsernameWithMixedCaseAndWhitespace_thenGatewayIsQueriedWithNormalizedUsername(){
		UserAuthPrincipal principal = superStaffPrincipal(TENANT_ID);
		StaffDomain target = staff(TARGET_ID);
		willReturn(Optional.of(target)).given(gateway)
				.findByUsername(any(UsernameDomain.class), any(TenantIdDomain.class));

		useCase.execute(new StaffDeleteRequest("  DaDaNg  "), principal, presenter);

		then(gateway).should()
				.findByUsername(eq(new UsernameDomain(USERNAME)), eq(new TenantIdDomain(TENANT_ID)));
	}

}
