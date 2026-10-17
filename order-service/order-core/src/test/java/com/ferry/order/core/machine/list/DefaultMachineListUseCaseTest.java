package com.ferry.order.core.machine.list;

import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineFilter;
import com.ferry.order.domain.machine.MachineListSortBy;
import com.ferry.order.domain.machine.MachineType;
import com.ferry.order.domain.machine.MachineUsage;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import com.ferry.utils.pagination.CursorCodec;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.PageCursor;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.PaginationConstant;
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
import java.util.Map;

import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.any;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@ExtendWith(MockitoExtension.class)
class DefaultMachineListUseCaseTest{

	private static final String TENANT_ID = "01TENANTANGGREK0000000000";
	private static final String STAFF_ID = "01STAFFROBERT000000000000";
	private static final String WASHER_ID = "01MACHINECUCIA00000000000";
	private static final String DRYER_ID = "01MACHINEKERINGB000000000";

	@Mock
	MachineListGateway gateway;
	@InjectMocks
	DefaultMachineListUseCase useCase;
	@Mock
	MachineListPresenter presenter;
	@Captor
	ArgumentCaptor<MachineFilter> filterCaptor;
	@Captor
	ArgumentCaptor<MachineListResponse> responseCaptor;

	@Test
	void givenNoFiltersAndNoCursor_thenSortsByNameAscendingAndShowsActiveOnly(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		MachineListRequest request = new MachineListRequest(null, null, null, null, null, null, null, null);
		willReturn(new CursorFetch<MachineUsage>(List.of(), false)).given(gateway)
				.findByFilter(any(MachineFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findByFilter(filterCaptor.capture());

		MachineFilter filter = filterCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(filter.tenantId()).isEqualTo(TENANT_ID);
			softly.then(filter.name()).isNull();
			softly.then(filter.type()).isNull();
			softly.then(filter.activeOnly()).isTrue();
			softly.then(filter.sortBy()).isEqualTo(MachineListSortBy.NAME);
			softly.then(filter.sortDir()).isEqualTo(SortDirection.ASC);
			softly.then(filter.pageDirection()).isEqualTo(PageDirection.NEXT);
			softly.then(filter.cursor()).isNull();
			softly.then(filter.pageSize()).isEqualTo(PaginationConstant.DEFAULT_PAGE_SIZE);
		});
	}

	@Test
	void givenFiltersAndBeforeCursor_thenPassesThemThroughToTheGateway(){
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		String cursorToken = CursorCodec.encode(WASHER_ID, WASHER_ID);
		MachineListRequest request = new MachineListRequest("mesin", MachineType.WASHER, false, null, cursorToken,
				MachineListSortBy.ID, SortDirection.DESC, 500);
		willReturn(new CursorFetch<MachineUsage>(List.of(), false)).given(gateway)
				.findByFilter(any(MachineFilter.class));

		useCase.execute(request, principal, presenter);

		then(gateway).should()
				.findByFilter(filterCaptor.capture());

		MachineFilter filter = filterCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(filter.name()).isEqualTo("mesin");
			softly.then(filter.nameStartsWith()).isEqualTo("mesin%");
			softly.then(filter.type()).isEqualTo(MachineType.WASHER);
			softly.then(filter.typeValue()).isEqualTo((short) 1);
			softly.then(filter.activeOnly()).isFalse();
			softly.then(filter.sortBy()).isEqualTo(MachineListSortBy.ID);
			softly.then(filter.sortDir()).isEqualTo(SortDirection.DESC);
			softly.then(filter.pageDirection()).isEqualTo(PageDirection.PREV);
			softly.then(filter.cursor()).isEqualTo(new PageCursor(WASHER_ID, WASHER_ID));
			softly.then(filter.pageSize()).isEqualTo(PaginationConstant.MAX_PAGE_SIZE);
		});
	}

	@Test
	void givenMoreRowsAfterACursor_thenPresentsUsageWithNameCursors(){
		Instant now = Instant.now();
		OrderAuthPrincipal principal = OrderAuthPrincipal.builder()
				.userId(STAFF_ID)
				.tenantId(TENANT_ID)
				.role(StaffRole.STAFF)
				.build();
		Machine washer = Machine.builder()
				.id(WASHER_ID)
				.tenantId(TENANT_ID)
				.name("Cuci A")
				.type(MachineType.WASHER)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		Machine dryer = Machine.builder()
				.id(DRYER_ID)
				.tenantId(TENANT_ID)
				.name("Kering B")
				.type(MachineType.DRYER)
				.active(true)
				.deleted(false)
				.createdAt(now)
				.createdBy(STAFF_ID)
				.updatedAt(now)
				.updatedBy(STAFF_ID)
				.build();
		List<Machine> machines = List.of(washer, dryer);
		List<MachineUsage> expectedUsages = List.of(new MachineUsage(washer, "INV-20261016-9PL2AA"),
				new MachineUsage(dryer, null));
		String afterToken = CursorCodec.encode("Bilas", "01MACHINEBILAS00000000000");
		MachineListRequest request = new MachineListRequest(null, null, null, afterToken, null, null, null, 2);
		willReturn(new CursorFetch<>(machines, true)).given(gateway)
				.findByFilter(any(MachineFilter.class));
		willReturn(Map.of(WASHER_ID, "INV-20261016-9PL2AA")).given(gateway)
				.getCurrentOrderNumberByMachineId(anyList());

		useCase.execute(request, principal, presenter);

		then(presenter).should()
				.present(responseCaptor.capture());

		MachineListResponse response = responseCaptor.getValue();

		thenSoftly(softly -> {
			softly.then(response.machines()).isEqualTo(expectedUsages);
			softly.then(response.machines().getFirst().inUse()).isTrue();
			softly.then(response.machines().getLast().inUse()).isFalse();
			softly.then(response.nextCursor()).isEqualTo(CursorCodec.encode("Kering B", DRYER_ID));
			softly.then(response.prevCursor()).isEqualTo(CursorCodec.encode("Cuci A", WASHER_ID));
		});
	}

}
