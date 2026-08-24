package com.ferry.order.webservice.order.complete;

import com.ferry.order.core.order.complete.OrderCompleteRequest;
import com.ferry.order.core.order.complete.OrderCompleteUseCase;
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
public class OrderCompleteWebController{
	private final OrderCompleteUseCase orderCompleteUseCase;

	@Transactional
	@PutMapping("/order/complete")
	public ResponseEntity<?> complete(@RequestBody OrderCompleteRequest request,
	                                  @AuthenticationPrincipal OrderAuthPrincipal principal){
		OrderCompleteWebPresenter presenter = new OrderCompleteWebPresenter();
		orderCompleteUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
