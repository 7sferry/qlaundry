package com.ferry.user.core.customer.list;

import com.ferry.user.domain.common.AddressLine;
import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Phone;
import com.ferry.user.domain.customer.*;
import com.ferry.user.domain.token.UserAuthPrincipal;
import com.ferry.utils.pagination.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultCustomerListUseCaseTest{

	private static final String TENANT_ID = "01TENANTGARUDA0000000000";
	private static final String CUSTOMER_ID_1 = "01CUSTOMERDEWI0000000000";
	private static final String CUSTOMER_ID_2 = "01CUSTOMERSUSANTO0000000";
	private static final String PRINCIPAL_ID = "01STAFFANGGARA000000000";

	@Mock
	CustomerListGateway gateway;
	@InjectMocks
	DefaultCustomerListUseCase useCase;
	@Mock
	CustomerListPresenter presenter;
	@Captor
	ArgumentCaptor<CustomerFilter> filterCaptor;
	@Captor
	ArgumentCaptor<CustomerListResponse> responseCaptor;

	@Test
	void givenNoCustomersFound_thenPresentsEmptyResponseWithoutQueryingContactInfo(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		willReturn(new CursorFetch<>(List.of(), false)).given(gateway)
				.findByFilter(any(CustomerFilter.class));

		useCase.execute(new CustomerListRequest(null, null, null, null, null, null, null), principal, presenter);

		then(gateway).should(never()).findEmailsByFilter(any(CustomerEmailFilter.class));
		then(gateway).should(never()).findPhonesByFilter(any(CustomerPhoneFilter.class));
		then(gateway).should(never()).findAddressesByFilter(any(CustomerAddressFilter.class));
		then(presenter).should()
				.present(new CustomerListResponse(List.of(), Map.of(), Map.of(), Map.of(), null, null));
	}

	@Test
	void givenCustomersFound_thenAggregatesContactInfoGroupedByCustomerId(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		Customer customer1 = Customer.register(TENANT_ID, new FullName("dewi anggraini"), null,
				PRINCIPAL_ID).toBuilder().id(CUSTOMER_ID_1).build();
		Customer customer2 = Customer.register(TENANT_ID, new FullName("susanto wijaya"), null,
				PRINCIPAL_ID).toBuilder().id(CUSTOMER_ID_2).build();
		willReturn(new CursorFetch<>(List.of(customer1, customer2), false)).given(gateway)
				.findByFilter(any(CustomerFilter.class));
		CustomerEmail email1 = CustomerEmail.register(CUSTOMER_ID_1,
				new Email("dewi@laundry.test"), PRINCIPAL_ID);
		willReturn(List.of(email1)).given(gateway)
				.findEmailsByFilter(any(CustomerEmailFilter.class));
		CustomerPhone phone1 = CustomerPhone.register(CUSTOMER_ID_1,
				new Phone("+6281122334455"), PRINCIPAL_ID);
		CustomerPhone phone2 = CustomerPhone.register(CUSTOMER_ID_2,
				new Phone("+6285566778899"), PRINCIPAL_ID);
		willReturn(List.of(phone1, phone2)).given(gateway)
				.findPhonesByFilter(any(CustomerPhoneFilter.class));
		CustomerAddress address2 = CustomerAddress.register(CUSTOMER_ID_2,
				new AddressLine("Jl. Garuda No. 12"), PRINCIPAL_ID);
		willReturn(List.of(address2)).given(gateway)
				.findAddressesByFilter(any(CustomerAddressFilter.class));

		useCase.execute(new CustomerListRequest(null, null, null, null, null, null, null), principal, presenter);

		then(presenter).should().present(responseCaptor.capture());

		CustomerListResponse response = responseCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(response.emailsByCustomerId()).containsEntry(CUSTOMER_ID_1, List.of(email1));
			softly.then(response.emailsByCustomerId()).doesNotContainKey(CUSTOMER_ID_2);
			softly.then(response.phonesByCustomerId()).containsEntry(CUSTOMER_ID_1, List.of(phone1));
			softly.then(response.phonesByCustomerId()).containsEntry(CUSTOMER_ID_2, List.of(phone2));
			softly.then(response.addressesByCustomerId()).containsEntry(CUSTOMER_ID_2, List.of(address2));
			softly.then(response.addressesByCustomerId()).doesNotContainKey(CUSTOMER_ID_1);
		});
	}

	@Test
	void givenFullNameProvided_thenQueriesGatewayWithFilterScopedToTenant(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		willReturn(new CursorFetch<>(List.of(), false)).given(gateway)
				.findByFilter(filterCaptor.capture());

		useCase.execute(new CustomerListRequest("dewi", null, null, null, null, null, null), principal, presenter);

		CustomerFilter filter = filterCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(filter.fullName()).isEqualTo("dewi");
			softly.then(filter.tenantId()).isEqualTo(TENANT_ID);
			softly.then(filter.phone()).isNull();
		});
	}

	@Test
	void givenBlankPhone_thenFilterPhoneIsNull(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		willReturn(new CursorFetch<>(List.of(), false)).given(gateway)
				.findByFilter(filterCaptor.capture());

		useCase.execute(new CustomerListRequest(null, "   ", null, null, null, null, null), principal, presenter);

		thenSoftly(softly -> softly.then(filterCaptor.getValue().phone()).isNull());
	}

	@Test
	void givenPhoneTypedTheWayPeopleSayIt_thenFilterPhoneIsNormalizedToE164(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		willReturn(new CursorFetch<>(List.of(), false)).given(gateway)
				.findByFilter(filterCaptor.capture());

		useCase.execute(new CustomerListRequest(null, "0811-2233-4455", null, null, null, null, null), principal, presenter);

		thenSoftly(softly -> softly.then(filterCaptor.getValue().phone()).isEqualTo("+6281122334455"));
	}

	@Test
	void givenNoSortOrCursorProvided_thenDefaultsToIdDescendingFirstPage(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		willReturn(new CursorFetch<>(List.of(), false)).given(gateway)
				.findByFilter(filterCaptor.capture());

		useCase.execute(new CustomerListRequest(null, null, null, null, null, null, null), principal, presenter);

		CustomerFilter filter = filterCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(filter.sortBy()).isEqualTo(CustomerListSortBy.ID);
			softly.then(filter.sortDir()).isEqualTo(SortDirection.DESC);
			softly.then(filter.pageDirection()).isEqualTo(PageDirection.NEXT);
			softly.then(filter.cursor()).isNull();
		});
	}

	@Test
	void givenMoreRowsThanPageSize_thenPresentsNextCursorButNoPrevCursorOnFirstPage(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		Customer customer = Customer.register(TENANT_ID, new FullName("dewi anggraini"), null,
				PRINCIPAL_ID).toBuilder().id(CUSTOMER_ID_1).build();
		willReturn(new CursorFetch<>(List.of(customer), true)).given(gateway)
				.findByFilter(any(CustomerFilter.class));
		willReturn(List.of()).given(gateway).findEmailsByFilter(any(CustomerEmailFilter.class));
		willReturn(List.of()).given(gateway).findPhonesByFilter(any(CustomerPhoneFilter.class));
		willReturn(List.of()).given(gateway).findAddressesByFilter(any(CustomerAddressFilter.class));

		useCase.execute(new CustomerListRequest(null, null, null, null, null, null, null), principal, presenter);

		then(presenter).should().present(responseCaptor.capture());
		CustomerListResponse response = responseCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(response.nextCursor()).isNotNull();
			softly.then(response.prevCursor()).isNull();
		});
	}

	@Test
	void givenCursorProvidedAndNoMoreRows_thenPresentsPrevCursorButNoNextCursor(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		Customer customer = Customer.register(TENANT_ID, new FullName("susanto wijaya"), null,
				PRINCIPAL_ID).toBuilder().id(CUSTOMER_ID_2).build();
		willReturn(new CursorFetch<>(List.of(customer), false)).given(gateway)
				.findByFilter(filterCaptor.capture());
		willReturn(List.of()).given(gateway).findEmailsByFilter(any(CustomerEmailFilter.class));
		willReturn(List.of()).given(gateway).findPhonesByFilter(any(CustomerPhoneFilter.class));
		willReturn(List.of()).given(gateway).findAddressesByFilter(any(CustomerAddressFilter.class));
		String cursor = CursorCodec.encode(CUSTOMER_ID_1, CUSTOMER_ID_1);

		useCase.execute(new CustomerListRequest(null, null, cursor, null, null, null, null),
				principal, presenter);

		then(presenter).should().present(responseCaptor.capture());
		CustomerListResponse response = responseCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(response.nextCursor()).isNull();
			softly.then(response.prevCursor()).isNotNull();
			softly.then(filterCaptor.getValue().cursor()).isEqualTo(new PageCursor(CUSTOMER_ID_1, CUSTOMER_ID_1));
		});
	}

	@Test
	void givenSortByNameAscendingWithPrevDirection_thenFilterCarriesThatSort(){
		UserAuthPrincipal principal = UserAuthPrincipal.builder().tenantId(TENANT_ID).build();
		Customer customer = Customer.register(TENANT_ID, new FullName("dewi anggraini"), null,
				PRINCIPAL_ID).toBuilder().id(CUSTOMER_ID_1).build();
		willReturn(new CursorFetch<>(List.of(customer), false)).given(gateway)
				.findByFilter(filterCaptor.capture());
		willReturn(List.of()).given(gateway).findEmailsByFilter(any(CustomerEmailFilter.class));
		willReturn(List.of()).given(gateway).findPhonesByFilter(any(CustomerPhoneFilter.class));
		willReturn(List.of()).given(gateway).findAddressesByFilter(any(CustomerAddressFilter.class));
		String cursor = CursorCodec.encode(CUSTOMER_ID_2, CUSTOMER_ID_2);

		useCase.execute(new CustomerListRequest(null, null, null, cursor, CustomerListSortBy.NAME,
				SortDirection.ASC, null), principal, presenter);

		CustomerFilter filter = filterCaptor.getValue();
		thenSoftly(softly -> {
			softly.then(filter.sortBy()).isEqualTo(CustomerListSortBy.NAME);
			softly.then(filter.sortDir()).isEqualTo(SortDirection.ASC);
			softly.then(filter.pageDirection()).isEqualTo(PageDirection.PREV);
		});
	}

}
