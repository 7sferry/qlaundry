package com.ferry.order.webservice.service.list;

import com.ferry.order.core.service.list.LaundryServiceListRequest;
import com.ferry.order.core.service.list.LaundryServiceListUseCase;
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
public class LaundryServiceListWebController{
	private final LaundryServiceListUseCase laundryServiceListUseCase;

	@Transactional(readOnly = true)
	@GetMapping("/service/list")
	public ResponseEntity<?> getList(LaundryServiceListRequest request,
	                                 @AuthenticationPrincipal OrderAuthPrincipal principal){
		LaundryServiceListWebPresenter presenter = new LaundryServiceListWebPresenter();
		laundryServiceListUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
