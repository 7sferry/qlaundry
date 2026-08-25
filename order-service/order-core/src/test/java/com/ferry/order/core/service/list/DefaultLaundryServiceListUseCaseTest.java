package com.ferry.order.core.service.list;

import com.ferry.order.domain.common.MoneyDomain;
import com.ferry.order.domain.common.NoteDomain;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceFilter;
import com.ferry.order.domain.service.ServiceCategory;
import com.ferry.order.domain.service.ServiceUnit;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import com.ferry.utils.pagination.CursorCodec;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.PageCursor;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortBy;
import com.ferry.utils.pagination.SortDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultLaundryServiceListUseCaseTest{

	private static final String TENANT_ID = "01TENANTDAHLIA000000000000";
	private static final String STAFF_ID = "01STAFFEKOSUSANTO00000000000";
	private static final String SERVICE_ID_1 = "01SERVICESATU00000000000000";
	private static final String SERVICE_ID_2 = "01SERVICEDUA00000000000000A";

	@Mock
	LaundryServiceListGateway gateway;
	@InjectMocks
	DefaultLaundryServiceListUseCase useCase;
	@Mock
	LaundryServiceListPresenter presenter;
	@Captor
	ArgumentCaptor<LaundryServiceFilter> filterCaptor;
	@Captor
	ArgumentCaptor<LaundryServiceListResponse> responseCaptor;

	@Test
	void givenNoFiltersAndNoCursor_thenBuildsFilterWithDefaultsAndActiveOnlyTrue(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceListRequest request = new LaundryServiceListRequest(null, null, null, null, null, null, null);
		willReturn(new CursorFetch<LaundryServiceDomain>(List.of(), false)).given(gateway)
				.findByFilter(any(LaundryServiceFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findByFilter(filterCaptor.capture());

		LaundryServiceFilter filter = filterCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(filter.tenantId()).isEqualTo(TENANT_ID);
			softly.then(filter.name()).isNull();
			softly.then(filter.category()).isNull();
			softly.then(filter.activeOnly()).isTrue();
			softly.then(filter.sortBy()).isEqualTo(SortBy.ID);
			softly.then(filter.sortDir()).isEqualTo(SortDirection.DESC);
			softly.then(filter.pageDirection()).isEqualTo(PageDirection.NEXT);
			softly.then(filter.cursor()).isNull();
		});
	}

	@Test
	void givenNameCategoryAndActiveOnlyFalse_thenPassesThemThroughToTheGateway(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceListRequest request = new LaundryServiceListRequest("cuci", ServiceCategory.DRY_CLEAN, false,
				null, null, null, null);
		willReturn(new CursorFetch<LaundryServiceDomain>(List.of(), false)).given(gateway)
				.findByFilter(any(LaundryServiceFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findByFilter(filterCaptor.capture());

		LaundryServiceFilter filter = filterCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(filter.name()).isEqualTo("cuci");
			softly.then(filter.category()).isEqualTo(ServiceCategory.DRY_CLEAN);
			softly.then(filter.activeOnly()).isFalse();
		});
	}

	@Test
	void givenExplicitCursorSortAndDirection_thenDecodesCursorAndAppliesRequestedSortAndDirection(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		String cursorToken = CursorCodec.encode("cuci kiloan", SERVICE_ID_1);
		LaundryServiceListRequest request = new LaundryServiceListRequest(null, null, null, cursorToken,
				PageDirection.PREV, SortBy.NAME, SortDirection.ASC);
		willReturn(new CursorFetch<LaundryServiceDomain>(List.of(), false)).given(gateway)
				.findByFilter(any(LaundryServiceFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findByFilter(filterCaptor.capture());

		LaundryServiceFilter filter = filterCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(filter.cursor()).isEqualTo(new PageCursor("cuci kiloan", SERVICE_ID_1));
			softly.then(filter.sortBy()).isEqualTo(SortBy.NAME);
			softly.then(filter.sortDir()).isEqualTo(SortDirection.ASC);
			softly.then(filter.pageDirection()).isEqualTo(PageDirection.PREV);
		});
	}

	@Test
	void givenServicesReturnedWithMoreRowsAndCursorProvided_thenPresentsPageWithNextAndPrevCursors(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		LaundryServiceDomain service1 = LaundryServiceDomain.builder()
				.id(SERVICE_ID_1)
				.tenantId(TENANT_ID)
				.name("Cuci Kiloan Reguler")
				.description(new NoteDomain("regular wash"))
				.pricePerUnit(MoneyDomain.of(7000L))
				.unit(ServiceUnit.KG)
				.category(ServiceCategory.WASH)
				.estimatedHours(24)
				.expressMultiplier(1.0d)
				.popular(true)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		LaundryServiceDomain service2 = LaundryServiceDomain.builder()
				.id(SERVICE_ID_2)
				.tenantId(TENANT_ID)
				.name("Setrika Satuan")
				.description(new NoteDomain("iron only"))
				.pricePerUnit(MoneyDomain.of(5000L))
				.unit(ServiceUnit.ITEM)
				.category(ServiceCategory.IRON)
				.estimatedHours(12)
				.expressMultiplier(1.5d)
				.popular(false)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		String cursorToken = CursorCodec.encode(SERVICE_ID_1, SERVICE_ID_1);
		LaundryServiceListRequest request = new LaundryServiceListRequest(null, null, null, cursorToken,
				PageDirection.NEXT, SortBy.ID, SortDirection.DESC);
		willReturn(new CursorFetch<>(List.of(service1, service2), true)).given(gateway)
				.findByFilter(any(LaundryServiceFilter.class));

		useCase.execute(request, principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		LaundryServiceListResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.services()).containsExactly(service1, service2);
			softly.then(response.nextCursor()).isEqualTo(CursorCodec.encode(SERVICE_ID_2, SERVICE_ID_2));
			softly.then(response.prevCursor()).isEqualTo(CursorCodec.encode(SERVICE_ID_1, SERVICE_ID_1));
		});
	}

}
