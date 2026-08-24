package com.ferry.user.core.staff.detail;

import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.common.exception.InvalidUsernameException;
import com.ferry.user.domain.common.exception.NotFoundException;
import com.ferry.user.domain.staff.StaffAddressFilter;
import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.StaffPhoneFilter;
import com.ferry.user.domain.staff.detail.StaffAddressDetailProjection;
import com.ferry.user.domain.staff.detail.StaffDetailProjection;
import com.ferry.user.domain.staff.detail.StaffEmailDetailProjection;
import com.ferry.user.domain.staff.detail.StaffPhoneDetailProjection;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultStaffDetailUseCaseTest{

	private static final String TENANT_ID = "tnt-semarang-06";
	private static final String USERNAME = "gunawansenior";
	private static final String STAFF_ID = "stf-303";
	private static final String FULL_NAME = "Gunawan Wibisono";

	@Mock
	StaffDetailGateway gateway;
	@InjectMocks
	DefaultStaffDetailUseCase useCase;
	@Mock
	StaffDetailPresenter presenter;
	@Captor
	ArgumentCaptor<StaffPhoneFilter> phoneFilterCaptor;
	@Captor
	ArgumentCaptor<StaffEmailFilter> emailFilterCaptor;
	@Captor
	ArgumentCaptor<StaffAddressFilter> addressFilterCaptor;
	@Captor
	ArgumentCaptor<StaffDetailResponse> responseCaptor;

	@Test
	void givenBlankUsername_thenThrowsConstraintViolationException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffDetailRequest(" "), principal, presenter))
				.isInstanceOf(ConstraintViolationException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenUsernameShorterThanMinimumLength_thenThrowsInvalidUsernameException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffDetailRequest("guna"), principal, presenter))
				.isInstanceOf(InvalidUsernameException.class));

		then(gateway).shouldHaveNoInteractions();
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenStaffNotFound_thenThrowsNotFoundException(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		willReturn(Optional.empty()).given(gateway).findDetail(new UsernameDomain(USERNAME), new TenantIdDomain(TENANT_ID));

		thenSoftly(softly -> softly.thenThrownBy(() -> useCase.execute(new StaffDetailRequest(USERNAME), principal, presenter))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Staff Not Found"));

		then(gateway).should(never()).findByFilter(any(StaffPhoneFilter.class));
		then(presenter).shouldHaveNoInteractions();
	}

	@Test
	void givenStaffFound_thenPresentsDetailWithPhonesEmailsAndAddresses(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		StaffDetailProjection detail = new StaffDetailProjection(STAFF_ID, "desc", FULL_NAME, Instant.now(), USERNAME);
		willReturn(Optional.of(detail)).given(gateway).findDetail(new UsernameDomain(USERNAME), new TenantIdDomain(TENANT_ID));
		List<StaffPhoneDetailProjection> phones = List.of(new StaffPhoneDetailProjection("081234567890"));
		List<StaffEmailDetailProjection> emails = List.of(new StaffEmailDetailProjection("gunawan@qlaundry.com"));
		List<StaffAddressDetailProjection> addresses = List.of(new StaffAddressDetailProjection("Jl. Pandanaran No. 8"));
		willReturn(phones).given(gateway).findByFilter(phoneFilterCaptor.capture());
		willReturn(emails).given(gateway).findByFilter(emailFilterCaptor.capture());
		willReturn(addresses).given(gateway).findByFilter(addressFilterCaptor.capture());

		useCase.execute(new StaffDetailRequest(USERNAME), principal, presenter);

		then(presenter).should().present(new StaffDetailResponse(detail, phones, emails, addresses));
		thenSoftly(softly -> {
			softly.then(phoneFilterCaptor.getValue().staffId()).isEqualTo(STAFF_ID);
			softly.then(emailFilterCaptor.getValue().staffId()).isEqualTo(STAFF_ID);
			softly.then(addressFilterCaptor.getValue().staffId()).isEqualTo(STAFF_ID);
		});
	}

	@Test
	void givenStaffFoundWithNoContactInfo_thenPresentsDetailWithEmptyLists(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		StaffDetailProjection detail = new StaffDetailProjection(STAFF_ID, "desc", FULL_NAME, Instant.now(), USERNAME);
		willReturn(Optional.of(detail)).given(gateway).findDetail(new UsernameDomain(USERNAME), new TenantIdDomain(TENANT_ID));
		willReturn(List.of()).given(gateway).findByFilter(any(StaffPhoneFilter.class));
		willReturn(List.of()).given(gateway).findByFilter(any(StaffEmailFilter.class));
		willReturn(List.of()).given(gateway).findByFilter(any(StaffAddressFilter.class));

		useCase.execute(new StaffDetailRequest(USERNAME), principal, presenter);

		then(presenter).should().present(responseCaptor.capture());
		StaffDetailResponse response = responseCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(response.phones()).isEmpty();
			softly.then(response.emails()).isEmpty();
			softly.then(response.addresses()).isEmpty();
		});
	}

}
