package com.ferry.order.webservice.order.confirm;

import com.ferry.order.core.order.confirm.OrderConfirmRequest;
import com.ferry.order.core.order.confirm.OrderConfirmUseCase;
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
public class OrderConfirmWebController{
	private final OrderConfirmUseCase orderConfirmUseCase;

	@Transactional
	@PutMapping("/order/confirm")
	public ResponseEntity<?> confirm(@RequestBody OrderConfirmRequest request,
	                                 @AuthenticationPrincipal OrderAuthPrincipal principal){
		OrderConfirmWebPresenter presenter = new OrderConfirmWebPresenter();
		orderConfirmUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
