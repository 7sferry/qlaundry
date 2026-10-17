package com.ferry.order.webservice.order.assignstaffs;

import com.ferry.order.core.order.assignstaffs.OrderAssignStaffsRequest;
import com.ferry.order.core.order.assignstaffs.OrderAssignStaffsUseCase;
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
public class OrderAssignStaffsController{
	private final OrderAssignStaffsUseCase orderAssignStaffsUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PatchMapping("/order/assignStaffs")
	public ResponseEntity<?> assignStaffs(@RequestBody OrderAssignStaffsRequest request,
	                                      @AuthenticationPrincipal OrderAuthPrincipal principal){
		WebOrderAssignStaffsPresenter presenter = new WebOrderAssignStaffsPresenter();
		orderAssignStaffsUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
