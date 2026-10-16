package com.ferry.order.webservice.order.machines;

import com.ferry.order.core.order.machines.OrderMachinesRequest;
import com.ferry.order.core.order.machines.OrderMachinesUseCase;
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
public class OrderMachinesController{
	private final OrderMachinesUseCase orderMachinesUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PatchMapping("/order/machines")
	public ResponseEntity<?> changeMachines(@RequestBody OrderMachinesRequest request,
	                                        @AuthenticationPrincipal OrderAuthPrincipal principal){
		WebOrderMachinesPresenter presenter = new WebOrderMachinesPresenter();
		orderMachinesUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
