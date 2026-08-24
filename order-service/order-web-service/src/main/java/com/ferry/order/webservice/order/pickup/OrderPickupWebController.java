package com.ferry.order.webservice.order.pickup;

import com.ferry.order.core.order.pickup.OrderPickupRequest;
import com.ferry.order.core.order.pickup.OrderPickupUseCase;
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
public class OrderPickupWebController{
	private final OrderPickupUseCase orderPickupUseCase;

	@Transactional
	@PutMapping("/order/pickup")
	public ResponseEntity<?> pickup(@RequestBody OrderPickupRequest request,
	                                @AuthenticationPrincipal OrderAuthPrincipal principal){
		OrderPickupWebPresenter presenter = new OrderPickupWebPresenter();
		orderPickupUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
