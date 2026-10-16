package com.ferry.order.webservice.machine.update;

import com.ferry.order.core.machine.update.MachineUpdatePresenter;
import com.ferry.order.core.machine.update.MachineUpdateResponse;
import com.ferry.order.domain.machine.Machine;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebMachineUpdatePresenter implements MachineUpdatePresenter{
	private ResponseEntity<MachineUpdateRestResponse> responseEntity;

	@Override
	public void present(MachineUpdateResponse response){
		Machine machine = response.machine();
		responseEntity = ResponseEntity.ok(new MachineUpdateRestResponse(machine.id(), machine.name(),
				machine.type().name(), machine.capacityKg(), machine.descriptionValue(), machine.active()));
	}

}
