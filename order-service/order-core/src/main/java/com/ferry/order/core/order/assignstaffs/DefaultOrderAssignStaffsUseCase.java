package com.ferry.order.core.order.assignstaffs;

import com.ferry.order.domain.common.exception.InvalidOrderStatusException;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderStaff;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderAssignStaffsUseCase implements OrderAssignStaffsUseCase{
	private final OrderAssignStaffsGateway gateway;

	@Override
	public void execute(OrderAssignStaffsRequest request, OrderAuthPrincipal principal,
	                    OrderAssignStaffsPresenter presenter){
		request.validate();
		OrderId orderId = new OrderId(request.orderId());
		TenantId tenantId = new TenantId(principal.tenantId());
		Order order = gateway.lockById(orderId, tenantId)
				.orElseThrow(() -> new NotFoundException("Order Not Found"));
		if(order.status() != OrderStatus.IN_PROGRESS){
			throw new InvalidOrderStatusException("Staff can only be changed while the order is in progress");
		}
		Set<String> requested = staffIds(request.staffIds(), principal);
		List<OrderStaff> all = gateway.findAllStaffsByOrderId(orderId);
		List<OrderStaff> current = all.stream()
				.filter(staff -> !staff.deleted())
				.toList();
		Map<String,OrderStaff> removedBefore = all.stream()
				.filter(OrderStaff::deleted)
				.collect(Collectors.toMap(OrderStaff::staffId, Function.identity(), (first, second) -> first));
		Set<String> currentValues = current.stream()
				.map(OrderStaff::staffId)
				.collect(Collectors.toSet());
		List<OrderStaff> kept = current.stream()
				.filter(staff -> requested.contains(staff.staffId()))
				.toList();
		List<OrderStaff> changes = new ArrayList<>(current.stream()
				.filter(staff -> !requested.contains(staff.staffId()))
				.map(staff -> staff.remove(principal.userId()))
				.toList());
		requested.stream()
				.filter(staffId -> !currentValues.contains(staffId))
				.map(staffId -> removedBefore.containsKey(staffId)
						? removedBefore.get(staffId).restore(principal.userId())
						: OrderStaff.assign(order.id(), staffId, principal.userId()))
				.forEach(changes::add);
		List<OrderStaff> saved = changes.isEmpty() ? List.of() : gateway.saveStaffs(changes);
		List<OrderStaff> staffs = new ArrayList<>(kept);
		saved.stream()
				.filter(staff -> !staff.deleted())
				.forEach(staffs::add);
		presenter.present(new OrderAssignStaffsResponse(order, staffs));
	}

	private Set<String> staffIds(Set<String> rawIds, OrderAuthPrincipal principal){
		Set<String> staffIds = rawIds == null ? Set.of() : rawIds.stream()
				.filter(id -> id != null && !id.isBlank())
				.map(String::trim)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return staffIds.isEmpty() ? Set.of(principal.userId()) : staffIds;
	}

}
