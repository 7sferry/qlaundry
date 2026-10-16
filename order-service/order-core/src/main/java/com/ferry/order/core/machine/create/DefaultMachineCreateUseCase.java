package com.ferry.order.core.machine.create;

import com.ferry.order.domain.common.Note;
import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import com.ferry.order.domain.common.exception.OrderForbiddenActionException;
import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.staff.StaffRole;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultMachineCreateUseCase implements MachineCreateUseCase{
	private final MachineCreateGateway gateway;

	@Override
	public void execute(MachineCreateRequest request, OrderAuthPrincipal principal, MachineCreatePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new OrderForbiddenActionException("Only super staff can manage machines");
		}
		request.validate();
		TenantId tenantId = new TenantId(principal.tenantId());
		Machine machine = Machine.create(tenantId.value(), request.name(), request.type(), request.capacityKg(),
				new Note(request.description()), principal.userId());
		if(gateway.existsByName(machine.name(), tenantId)){
			throw new InvalidOrderStateException("Machine name already exists");
		}
		Machine saved = gateway.save(machine);
		presenter.present(new MachineCreateResponse(saved));
	}

}
