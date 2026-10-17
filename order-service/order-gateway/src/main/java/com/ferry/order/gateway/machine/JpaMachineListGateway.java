package com.ferry.order.gateway.machine;

import com.ferry.order.core.machine.list.MachineListGateway;
import com.ferry.order.domain.machine.*;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.gateway.machine.entity.MachineJpa;
import com.ferry.order.gateway.machine.repository.MachineJpaRepository;
import com.ferry.order.gateway.order.repository.OrderMachineJpaRepository;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortDirection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaMachineListGateway implements MachineListGateway{
	private final MachineJpaRepository machineJpaRepository;
	private final OrderMachineJpaRepository orderMachineJpaRepository;

	@Override
	public CursorFetch<Machine> findByFilter(MachineFilter filter){
		List<Machine> rows = fetchByFilter(filter).stream()
				.map(MachineJpa::construct)
				.toList();
		return CursorFetch.of(rows, filter.pageSize(), filter.pageDirection());
	}

	@Override
	public Map<String,String> getCurrentOrderNumberByMachineId(List<Machine> machines){
		if(machines.isEmpty()){
			return Map.of();
		}
		List<String> machineIds = machines.stream().map(Machine::id).toList();
		return orderMachineJpaRepository.findOccupancies(machineIds, OrderStatus.IN_PROGRESS.getValue()).stream()
				.collect(Collectors.toMap(MachineOccupancy::machineId, MachineOccupancy::orderNumber,
						(first, _) -> first));
	}

	private List<MachineJpa> fetchByFilter(MachineFilter filter){
		Pageable pageable = PageRequest.ofSize(filter.pageSize() + 1);
		boolean forward = filter.pageDirection() == PageDirection.NEXT;
		boolean ascending = filter.sortDir() == SortDirection.ASC;
		boolean useAfterQuery = forward == ascending;
		if(filter.sortBy() == MachineListSortBy.NAME){
			return useAfterQuery
					? machineJpaRepository.findAfterByName(filter, pageable)
					: machineJpaRepository.findBeforeByName(filter, pageable);
		}
		return useAfterQuery
				? machineJpaRepository.findAfterById(filter, pageable)
				: machineJpaRepository.findBeforeById(filter, pageable);
	}

}
