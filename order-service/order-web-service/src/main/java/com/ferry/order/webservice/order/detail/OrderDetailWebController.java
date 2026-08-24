package com.ferry.order.webservice.order.detail;

import com.ferry.order.core.order.detail.OrderDetailRequest;
import com.ferry.order.core.order.detail.OrderDetailUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class OrderDetailWebController{
	private final OrderDetailUseCase orderDetailUseCase;

	@Transactional(readOnly = true)
	@GetMapping("/order/detail")
	public ResponseEntity<?> getDetail(OrderDetailRequest request, @AuthenticationPrincipal OrderAuthPrincipal principal){
		OrderDetailWebPresenter presenter = new OrderDetailWebPresenter();
		orderDetailUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
