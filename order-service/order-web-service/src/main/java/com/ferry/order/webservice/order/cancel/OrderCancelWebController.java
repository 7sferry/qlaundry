package com.ferry.order.webservice.order.cancel;

import com.ferry.order.core.order.cancel.OrderCancelRequest;
import com.ferry.order.core.order.cancel.OrderCancelUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class OrderCancelWebController{
	private final OrderCancelUseCase orderCancelUseCase;

	@Transactional
	@PutMapping("/order/cancel")
	public ResponseEntity<?> cancel(@RequestBody OrderCancelRequest request,
	                                @AuthenticationPrincipal OrderAuthPrincipal principal){
		OrderCancelWebPresenter presenter = new OrderCancelWebPresenter();
		orderCancelUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
