package com.ferry.order.core.machine.update;

import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import com.ferry.order.domain.common.exception.MachineBusyException;
import com.ferry.order.domain.common.exception.NotFoundException;
import com.ferry.order.domain.common.exception.OrderForbiddenActionException;
import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineId;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultMachineUpdateUseCase implements MachineUpdateUseCase{
	private final MachineUpdateGateway gateway;

	@Override
	public void execute(MachineUpdateRequest request, OrderAuthPrincipal principal, MachineUpdatePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new OrderForbiddenActionException("Only super staff can manage machines");
		}
		request.validate();
		MachineId machineId = new MachineId(request.machineId());
		TenantId tenantId = new TenantId(principal.tenantId());
		Machine machine = gateway.lockById(machineId, tenantId)
				.orElseThrow(() -> new NotFoundException("Machine Not Found"));
		boolean active = request.active() == null || request.active();
		Machine updated = machine.update(request.name(), request.type(),
				new Note(request.description()), active, principal.userId());
		if(gateway.existsByNameExcept(updated.name(), tenantId, machineId)){
			throw new InvalidOrderStateException("Machine name already exists");
		}
		if(machine.active() && !active){
			gateway.findCurrentOrderNumber(machineId).ifPresent(orderNumber -> {
				throw new MachineBusyException("Machine " + machine.name() + " is in use by " + orderNumber
						+ " and cannot be deactivated");
			});
		}
		Machine saved = gateway.save(updated);
		presenter.present(new MachineUpdateResponse(saved));
	}

}
