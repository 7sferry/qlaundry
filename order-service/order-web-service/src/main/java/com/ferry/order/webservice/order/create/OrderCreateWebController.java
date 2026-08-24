package com.ferry.order.webservice.order.create;

import com.ferry.order.core.order.create.OrderCreateRequest;
import com.ferry.order.core.order.create.OrderCreateUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class OrderCreateWebController{
	private final OrderCreateUseCase orderCreateUseCase;

	@Transactional
	@PostMapping("/order/create")
	public ResponseEntity<?> create(@RequestBody OrderCreateRequest request,
	                                @AuthenticationPrincipal OrderAuthPrincipal principal){
		OrderCreateWebPresenter presenter = new OrderCreateWebPresenter();
		orderCreateUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
