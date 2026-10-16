package com.ferry.order.webservice.machine.create;

import com.ferry.order.core.machine.create.MachineCreateRequest;
import com.ferry.order.core.machine.create.MachineCreateUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class MachineCreateController{
	private final MachineCreateUseCase machineCreateUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PostMapping("/machine/create")
	public ResponseEntity<?> create(@RequestBody MachineCreateRequest request,
	                                @AuthenticationPrincipal OrderAuthPrincipal principal){
		WebMachineCreatePresenter presenter = new WebMachineCreatePresenter();
		machineCreateUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
