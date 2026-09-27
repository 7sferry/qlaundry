package com.ferry.user.core.tenant.detail;

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
class DefaultTenantDetailUseCaseTest{

	private static final String TENANT_ID = "tenant-angel-laundry";
	private static final String PRINCIPAL_ID = "01PRINCIPALANGEL0000000000";

	@Mock
	TenantDetailGateway gateway;
	@InjectMocks
	DefaultTenantDetailUseCase useCase;
	@Mock
	TenantDetailPresenter presenter;
	@Captor
	ArgumentCaptor<TenantDetailResponse> responseCaptor;

	private UserAuthPrincipal principal(StaffRole role){
		return UserAuthPrincipal.builder()
				.userId(PRINCIPAL_ID)
				.tenantId(TENANT_ID)
				.role(role)
				.build();
	}

	private Tenant tenant(){
		Instant now = Instant.now();
		return new Tenant(TENANT_ID, new Username("angel"), new FullName("Angel Laundry"),
				new Description("Laundry kiloan cepat"), ZoneId.of("Asia/Jakarta"), TenantStatus.ACTIVE, 3, false,
				now, PRINCIPAL_ID, now, PRINCIPAL_ID);
	}

	@Test
	void givenNonSuperStaffRole_thenThrowsForbiddenActionException(){
		UserAuthPrincipal principal = principal(StaffRole.STAFF);

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(principal, presenter))
				.isInstanceOf(ForbiddenActionException.class)
				.hasMessage("Only super staff can view tenant settings"));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).should(never())
				.present(any(TenantDetailResponse.class));
	}

	@Test
	void givenTenantNotFound_thenThrowsNotFoundException(){
		UserAuthPrincipal principal = principal(StaffRole.SUPER_STAFF);
		willReturn(Optional.empty()).given(gateway)
				.findByTenantId(any(TenantId.class));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Tenant Not Found"));

		then(presenter).should(never())
				.present(any(TenantDetailResponse.class));
	}

	@Test
	void givenSuperStaffAndExistingTenant_thenPresentsDetail(){
		UserAuthPrincipal principal = principal(StaffRole.SUPER_STAFF);
		Tenant tenant = tenant();
		willReturn(Optional.of(tenant)).given(gateway)
				.findByTenantId(new TenantId(TENANT_ID));

		useCase.execute(principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());
		TenantDetailResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.tenant().fullNameValue()).isEqualTo("Angel Laundry");
			softly.then(response.tenant().descriptionValue()).isEqualTo("Laundry kiloan cepat");
			softly.then(response.tenant().timeZone()).isEqualTo(ZoneId.of("Asia/Jakarta"));
		});
	}

}
