package com.ferry.order.webservice.order.schedule;

import com.ferry.order.core.order.schedule.OrderScheduleRequest;
import com.ferry.order.core.order.schedule.OrderScheduleUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RestController
@RequiredArgsConstructor
public class OrderScheduleController{
	private final OrderScheduleUseCase orderScheduleUseCase;

	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	@GetMapping("/order/schedule")
	public ResponseEntity<?> getSchedule(OrderScheduleRequest request,
	                                     @AuthenticationPrincipal OrderAuthPrincipal principal){
		WebOrderSchedulePresenter presenter = new WebOrderSchedulePresenter();
		orderScheduleUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
