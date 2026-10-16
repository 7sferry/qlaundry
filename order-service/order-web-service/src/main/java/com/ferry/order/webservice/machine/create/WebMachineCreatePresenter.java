package com.ferry.order.webservice.machine.create;

import com.ferry.order.core.machine.create.MachineCreatePresenter;
import com.ferry.order.core.machine.create.MachineCreateResponse;
import com.ferry.order.domain.machine.Machine;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebMachineCreatePresenter implements MachineCreatePresenter{
	private ResponseEntity<MachineCreateRestResponse> responseEntity;

	@Override
	public void present(MachineCreateResponse response){
		Machine machine = response.machine();
		responseEntity = ResponseEntity.ok(new MachineCreateRestResponse(machine.id(), machine.name(),
				machine.type().name(), machine.capacityKg(), machine.descriptionValue(), machine.active()));
	}

}
