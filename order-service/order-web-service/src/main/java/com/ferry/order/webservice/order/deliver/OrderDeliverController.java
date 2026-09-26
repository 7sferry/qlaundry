package com.ferry.order.webservice.order.deliver;

import com.ferry.order.core.order.deliver.OrderDeliverRequest;
import com.ferry.order.core.order.deliver.OrderDeliverUseCase;
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
public class OrderDeliverController{
	private final OrderDeliverUseCase orderDeliverUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PatchMapping("/order/deliver")
	public ResponseEntity<?> deliver(@RequestBody OrderDeliverRequest request,
	                                 @AuthenticationPrincipal OrderAuthPrincipal principal){
		WebOrderDeliverPresenter presenter = new WebOrderDeliverPresenter();
		orderDeliverUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
