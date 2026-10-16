package com.ferry.order.webservice.order.assignmachines;

import com.ferry.order.core.order.assignmachines.OrderAssignMachinesRequest;
import com.ferry.order.core.order.assignmachines.OrderAssignMachinesUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class OrderAssignMachinesController{
	private final OrderAssignMachinesUseCase orderAssignMachinesUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PatchMapping("/order/assignMachines")
	public ResponseEntity<?> assignMachines(@RequestBody OrderAssignMachinesRequest request,
	                                        @AuthenticationPrincipal OrderAuthPrincipal principal){
		WebOrderAssignMachinesPresenter presenter = new WebOrderAssignMachinesPresenter();
		orderAssignMachinesUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
