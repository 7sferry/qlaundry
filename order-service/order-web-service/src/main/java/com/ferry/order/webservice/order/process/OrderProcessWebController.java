package com.ferry.order.webservice.order.process;

import com.ferry.order.core.order.process.OrderProcessRequest;
import com.ferry.order.core.order.process.OrderProcessUseCase;
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
public class OrderProcessWebController{
	private final OrderProcessUseCase orderProcessUseCase;

	@Transactional
	@PutMapping("/order/process")
	public ResponseEntity<?> process(@RequestBody OrderProcessRequest request,
	                                 @AuthenticationPrincipal OrderAuthPrincipal principal){
		OrderProcessWebPresenter presenter = new OrderProcessWebPresenter();
		orderProcessUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
