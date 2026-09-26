package com.ferry.order.webservice.service.create;

import com.ferry.order.core.service.create.LaundryServiceCreateRequest;
import com.ferry.order.core.service.create.LaundryServiceCreateUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
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
public class LaundryServiceCreateController{
	private final LaundryServiceCreateUseCase laundryServiceCreateUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PostMapping("/service/create")
	public ResponseEntity<?> create(@RequestBody LaundryServiceCreateRequest request,
	                                @AuthenticationPrincipal OrderAuthPrincipal principal){
		WebLaundryServiceCreatePresenter presenter = new WebLaundryServiceCreatePresenter();
		laundryServiceCreateUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
