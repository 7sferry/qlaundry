package com.ferry.order.webservice.machine.update;

import com.ferry.order.core.machine.update.MachineUpdateRequest;
import com.ferry.order.core.machine.update.MachineUpdateUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class MachineUpdateController{
	private final MachineUpdateUseCase machineUpdateUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PutMapping("/machine/update")
	public ResponseEntity<?> update(@RequestBody MachineUpdateRequest request,
	                                @AuthenticationPrincipal OrderAuthPrincipal principal){
		WebMachineUpdatePresenter presenter = new WebMachineUpdatePresenter();
		machineUpdateUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
