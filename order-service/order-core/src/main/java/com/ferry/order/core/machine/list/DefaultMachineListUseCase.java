package com.ferry.order.core.machine.list;

import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineFilter;
import com.ferry.order.domain.machine.MachineListSortBy;
import com.ferry.order.domain.machine.MachineUsage;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import com.ferry.utils.pagination.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultMachineListUseCase implements MachineListUseCase{
	private final MachineListGateway gateway;

	@Override
	public void execute(MachineListRequest request, OrderAuthPrincipal principal, MachineListPresenter presenter){
		request.validate();
		TenantId tenantId = new TenantId(principal.tenantId());
		MachineListSortBy sortBy = request.sortBy() == null ? MachineListSortBy.NAME : request.sortBy();
		SortDirection sortDir = request.sortDir() == null ? SortDirection.ASC : request.sortDir();
		PageDirection direction = PageDirection.direction(request.before());
		PageCursor cursor = PageCursor.cursor(request.after(), request.before());
		int pageSize = PaginationConstant.resolvePageSize(request.pageSize());
		MachineFilter filter = MachineFilter.builder()
				.tenantId(tenantId.value())
				.name(request.name())
				.type(request.type())
				.activeOnly(request.activeOnly() == null || request.activeOnly())
				.sortBy(sortBy)
				.sortDir(sortDir)
				.pageDirection(direction)
				.cursor(cursor)
				.pageSize(pageSize)
				.build();
		CursorFetch<MachineUsage> fetch = fetchMachineUsages(filter);
		CursorPage<MachineUsage> page = CursorPaginator.paginate(fetch, direction, cursor != null,
				row -> List.of(sortBy == MachineListSortBy.NAME ? row.machine().name() : row.machine().id(),
						row.machine().id()));
		presenter.present(new MachineListResponse(page.items(), page.nextCursor(), page.prevCursor()));
	}

	private CursorFetch<MachineUsage> fetchMachineUsages(MachineFilter filter){
		CursorFetch<Machine> machineCursorFetch = gateway.findByFilter(filter);
		List<Machine> machines = machineCursorFetch.rows();
		Map<String, String> currentOrderNumberByMachineId = gateway.getCurrentOrderNumberByMachineId(machines);
		List<MachineUsage> machineUsages = machines.stream()
				.map(entity -> new MachineUsage(entity, currentOrderNumberByMachineId.get(entity.id())))
				.toList();
		return new CursorFetch<>(machineUsages, machineCursorFetch.hasMore());
	}

}
