package com.ferry.order.webservice.order.ready;

import com.ferry.order.core.order.ready.OrderReadyRequest;
import com.ferry.order.core.order.ready.OrderReadyUseCase;
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
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class OrderReadyWebController{
	private final OrderReadyUseCase orderReadyUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PatchMapping("/order/ready")
	public ResponseEntity<?> markReady(@RequestBody OrderReadyRequest request,
	                                   @AuthenticationPrincipal OrderAuthPrincipal principal){
		OrderReadyWebPresenter presenter = new OrderReadyWebPresenter();
		orderReadyUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
