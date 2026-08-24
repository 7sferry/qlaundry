package com.ferry.order.webservice.order.payment;

import com.ferry.order.core.order.payment.OrderPaymentRequest;
import com.ferry.order.core.order.payment.OrderPaymentUseCase;
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
public class OrderPaymentWebController{
	private final OrderPaymentUseCase orderPaymentUseCase;

	@Transactional
	@PutMapping("/order/payment")
	public ResponseEntity<?> pay(@RequestBody OrderPaymentRequest request,
	                             @AuthenticationPrincipal OrderAuthPrincipal principal){
		OrderPaymentWebPresenter presenter = new OrderPaymentWebPresenter();
		orderPaymentUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
