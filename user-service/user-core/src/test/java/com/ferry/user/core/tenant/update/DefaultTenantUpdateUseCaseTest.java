package com.ferry.user.core.tenant.update;

import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.common.exception.ForbiddenActionException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.TenantStatus;
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
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultTenantUpdateUseCaseTest{

	private static final String TENANT_ID = "tenant-budi-laundry";
	private static final String PRINCIPAL_ID = "01PRINCIPALBUDI0000000000";

	@Mock
	TenantUpdateGateway gateway;
	@InjectMocks
	DefaultTenantUpdateUseCase useCase;
	@Mock
	TenantUpdatePresenter presenter;
	@Captor
	ArgumentCaptor<Tenant> tenantCaptor;
	@Captor
	ArgumentCaptor<TenantUpdateResponse> responseCaptor;

	private UserAuthPrincipal principal(StaffRole role){
		return UserAuthPrincipal.builder()
				.userId(PRINCIPAL_ID)
				.tenantId(TENANT_ID)
				.role(role)
				.build();
	}

	private Tenant existingTenant(){
		Instant now = Instant.now();
		return new Tenant(TENANT_ID, new Username("budi"), new FullName("Budi Bersih Laundry"),
				new Description("Laundry kiloan"), ZoneId.of("UTC"), TenantStatus.ACTIVE, 5, false,
				now, PRINCIPAL_ID, now, PRINCIPAL_ID);
	}

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		UserAuthPrincipal principal = principal(StaffRole.STAFF);
		TenantUpdateRequest request = new TenantUpdateRequest("Budi Bersih Laundry", "Laundry kiloan",
				ZoneId.of("Asia/Jakarta"));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ForbiddenActionException.class)
				.hasMessage("Only super staff can update tenant settings"));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(TenantUpdateResponse.class));
	}

	@Test
	void givenBlankTenantName_thenThrowsConstraintViolationException(){
		UserAuthPrincipal principal = principal(StaffRole.SUPER_STAFF);
		TenantUpdateRequest request = new TenantUpdateRequest(" ", "Laundry kiloan", ZoneId.of("Asia/Jakarta"));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(TenantUpdateResponse.class));
	}

	@Test
	void givenNullTimeZone_thenThrowsConstraintViolationException(){
		UserAuthPrincipal principal = principal(StaffRole.SUPER_STAFF);
		TenantUpdateRequest request = new TenantUpdateRequest("Budi Bersih Laundry", "Laundry kiloan", null);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(TenantUpdateResponse.class));
	}

	@Test
	void givenTenantNotFound_thenThrowsNotFoundException(){
		UserAuthPrincipal principal = principal(StaffRole.SUPER_STAFF);
		TenantUpdateRequest request = new TenantUpdateRequest("Budi Bersih Laundry", "Laundry kiloan",
				ZoneId.of("Asia/Jakarta"));
		willReturn(Optional.empty()).given(gateway)
				.findByTenantId(any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(request, principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Tenant Not Found"));

		then(gateway).should(never())
				.save(any(Tenant.class));
		then(presenter).should(never())
				.present(any(TenantUpdateResponse.class));
	}

	@Test
	void givenValidRequest_thenUpdatesTenantAndPresentsResult(){
		UserAuthPrincipal principal = principal(StaffRole.SUPER_STAFF);
		Tenant existing = existingTenant();
		TenantUpdateRequest request = new TenantUpdateRequest("Budi Kilat Laundry", "Laundry ekspres",
				ZoneId.of("Asia/Makassar"));
		willReturn(Optional.of(existing)).given(gateway)
				.findByTenantId(new TenantId(TENANT_ID));
		willReturn(existing).given(gateway)
				.save(any(Tenant.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.save(tenantCaptor.capture());
		then(presenter).should()
				.present(responseCaptor.capture());

		Tenant saved = tenantCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(saved.id()).isEqualTo(TENANT_ID);
			softly.then(saved.fullNameValue()).isEqualTo("Budi Kilat Laundry");
			softly.then(saved.descriptionValue()).isEqualTo("Laundry ekspres");
			softly.then(saved.timeZone()).isEqualTo(ZoneId.of("Asia/Makassar"));
			softly.then(saved.updatedBy()).isEqualTo(PRINCIPAL_ID);
			softly.then(saved.version()).isEqualTo(5);
		});
	}

}
