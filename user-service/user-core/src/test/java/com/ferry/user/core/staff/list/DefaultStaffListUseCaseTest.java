package com.ferry.user.core.staff.list;

import com.ferry.user.domain.staff.StaffAddressFilter;
import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.StaffFilter;
import com.ferry.user.domain.staff.StaffPhoneFilter;
import com.ferry.user.domain.staff.list.StaffAddressListProjection;
import com.ferry.user.domain.staff.list.StaffEmailListProjection;
import com.ferry.user.domain.staff.list.StaffListProjection;
import com.ferry.user.domain.staff.list.StaffPhoneListProjection;
import com.ferry.user.domain.token.UserAuthPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultStaffListUseCaseTest{

	private static final String TENANT_ID = "tnt-yogyakarta-05";
	private static final String STAFF_ID_1 = "stf-101";
	private static final String STAFF_ID_2 = "stf-102";
	private static final String FULL_NAME_1 = "Eka Prasetyo";
	private static final String FULL_NAME_2 = "Fitri Handayani";
	private static final String USERNAME_1 = "ekaprasetyo";
	private static final String USERNAME_2 = "fitrihandayani";

	@Mock
	StaffListGateway gateway;
	@InjectMocks
	DefaultStaffListUseCase useCase;
	@Mock
	StaffListPresenter presenter;
	@Captor
	ArgumentCaptor<StaffFilter> filterCaptor;
	@Captor
	ArgumentCaptor<StaffListResponse> responseCaptor;

	@Test
	void givenNoStaffFound_thenPresentsEmptyResponseWithoutQueryingContactInfo(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		willReturn(List.of()).given(gateway).findByFilter(any(StaffFilter.class));

		useCase.execute(new StaffListRequest(null), principal, presenter);

		then(gateway).should(never()).findPhonesByFilter(any());
		then(gateway).should(never()).findEmailsByFilter(any());
		then(gateway).should(never()).findAddressesByFilter(any());
		then(presenter).should().present(new StaffListResponse(List.of(), Map.of(), Map.of(), Map.of()));
	}

	@Test
	void givenStaffFound_thenAggregatesContactInfoGroupedByStaffId(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		StaffListProjection staff1 = new StaffListProjection(STAFF_ID_1, "desc one", FULL_NAME_1, Instant.now(), USERNAME_1);
		StaffListProjection staff2 = new StaffListProjection(STAFF_ID_2, "desc two", FULL_NAME_2, Instant.now(), USERNAME_2);
		willReturn(List.of(staff1, staff2)).given(gateway).findByFilter(any(StaffFilter.class));
		StaffPhoneListProjection phone1 = new StaffPhoneListProjection(STAFF_ID_1, "081111111111");
		StaffPhoneListProjection phone2 = new StaffPhoneListProjection(STAFF_ID_1, "082222222222");
		StaffPhoneListProjection phone3 = new StaffPhoneListProjection(STAFF_ID_2, "083333333333");
		willReturn(List.of(phone1, phone2, phone3)).given(gateway).findPhonesByFilter(any(StaffPhoneFilter.class));
		StaffEmailListProjection email1 = new StaffEmailListProjection(STAFF_ID_1, "eka@qlaundry.com");
		willReturn(List.of(email1)).given(gateway).findEmailsByFilter(any(StaffEmailFilter.class));
		StaffAddressListProjection address1 = new StaffAddressListProjection(STAFF_ID_2, "Jl. Malioboro No. 5");
		willReturn(List.of(address1)).given(gateway).findAddressesByFilter(any(StaffAddressFilter.class));

		useCase.execute(new StaffListRequest(null), principal, presenter);

		then(presenter).should().present(responseCaptor.capture());

		StaffListResponse response = responseCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(response.phonesByStaffId()).containsEntry(STAFF_ID_1, List.of(phone1, phone2));
			softly.then(response.phonesByStaffId()).containsEntry(STAFF_ID_2, List.of(phone3));
			softly.then(response.emailsByStaffId()).containsEntry(STAFF_ID_1, List.of(email1));
			softly.then(response.emailsByStaffId()).doesNotContainKey(STAFF_ID_2);
			softly.then(response.addressesByStaffId()).containsEntry(STAFF_ID_2, List.of(address1));
			softly.then(response.addressesByStaffId()).doesNotContainKey(STAFF_ID_1);
		});
	}

	@Test
	void givenFullNameAndTenantProvided_thenQueriesGatewayWithFilterScopedToTenant(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		willReturn(List.of()).given(gateway).findByFilter(filterCaptor.capture());

		useCase.execute(new StaffListRequest("eka"), principal, presenter);

		StaffFilter filter = filterCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(filter.fullName()).isEqualTo("eka");
			softly.then(filter.tenantId()).isEqualTo(TENANT_ID);
		});
	}

	@Test
	void givenStaffWithoutAnyContactInfo_thenContactMapsRemainEmpty(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		StaffListProjection staff = new StaffListProjection(STAFF_ID_1, "desc", FULL_NAME_1, Instant.now(), USERNAME_1);
		willReturn(List.of(staff)).given(gateway).findByFilter(any(StaffFilter.class));
		willReturn(List.of()).given(gateway).findPhonesByFilter(any(StaffPhoneFilter.class));
		willReturn(List.of()).given(gateway).findEmailsByFilter(any(StaffEmailFilter.class));
		willReturn(List.of()).given(gateway).findAddressesByFilter(any(StaffAddressFilter.class));

		useCase.execute(new StaffListRequest(null), principal, presenter);

		then(presenter).should().present(responseCaptor.capture());
		StaffListResponse response = responseCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(response.phonesByStaffId()).isEmpty();
			softly.then(response.emailsByStaffId()).isEmpty();
			softly.then(response.addressesByStaffId()).isEmpty();
		});
	}

}
