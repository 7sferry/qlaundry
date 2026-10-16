package com.ferry.order.webservice.machine.list;

import com.ferry.order.core.machine.list.MachineListRequest;
import com.ferry.order.core.machine.list.MachineListUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class MachineListController{
	private final MachineListUseCase machineListUseCase;

	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	@GetMapping("/machine/list")
	public ResponseEntity<?> getList(MachineListRequest request, @AuthenticationPrincipal OrderAuthPrincipal principal){
		WebMachineListPresenter presenter = new WebMachineListPresenter();
		machineListUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
