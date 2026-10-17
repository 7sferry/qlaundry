package com.ferry.order.webservice.machine.list;

import com.ferry.order.core.machine.list.MachineListPresenter;
import com.ferry.order.core.machine.list.MachineListResponse;
import com.ferry.order.webservice.machine.list.MachineListRestResponse.Machine;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebMachineListPresenter implements MachineListPresenter{
	private ResponseEntity<MachineListRestResponse> responseEntity;

	@Override
	public void present(MachineListResponse response){
		List<Machine> machines = response.machines().stream()
				.map(o -> new Machine(o.machine().id(), o.machine().name(), o.machine().type().name(),
						o.machine().descriptionValue(), o.machine().active(), o.inUse(), o.currentOrderNumber()))
				.toList();
		responseEntity = ResponseEntity.ok(new MachineListRestResponse(machines, response.nextCursor(),
				response.prevCursor()));
	}

}
