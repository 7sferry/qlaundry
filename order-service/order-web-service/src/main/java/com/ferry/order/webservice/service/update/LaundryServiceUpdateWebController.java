package com.ferry.order.webservice.service.update;

import com.ferry.order.core.service.update.LaundryServiceUpdateRequest;
import com.ferry.order.core.service.update.LaundryServiceUpdateUseCase;
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
public class LaundryServiceUpdateWebController{
	private final LaundryServiceUpdateUseCase laundryServiceUpdateUseCase;

	@Transactional
	@PutMapping("/service/update")
	public ResponseEntity<?> update(@RequestBody LaundryServiceUpdateRequest request,
	                                @AuthenticationPrincipal OrderAuthPrincipal principal){
		LaundryServiceUpdateWebPresenter presenter = new LaundryServiceUpdateWebPresenter();
		laundryServiceUpdateUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
