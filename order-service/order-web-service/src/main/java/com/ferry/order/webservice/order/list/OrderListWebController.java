package com.ferry.order.webservice.order.list;

import com.ferry.order.core.order.list.OrderListRequest;
import com.ferry.order.core.order.list.OrderListUseCase;
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
public class OrderListWebController{
	private final OrderListUseCase orderListUseCase;

	@Transactional(readOnly = true)
	@GetMapping("/order/list")
	public ResponseEntity<?> getList(OrderListRequest request, @AuthenticationPrincipal OrderAuthPrincipal principal){
		OrderListWebPresenter presenter = new OrderListWebPresenter();
		orderListUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
