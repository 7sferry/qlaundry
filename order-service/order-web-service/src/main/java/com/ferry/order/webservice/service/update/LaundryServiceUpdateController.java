package com.ferry.order.webservice.service.update;

import com.ferry.order.core.service.update.LaundryServiceUpdateRequest;
import com.ferry.order.core.service.update.LaundryServiceUpdateUseCase;
import com.ferry.order.domain.token.OrderAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
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
public class LaundryServiceUpdateController{
	private final LaundryServiceUpdateUseCase laundryServiceUpdateUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PutMapping("/service/update")
	public ResponseEntity<?> update(@RequestBody LaundryServiceUpdateRequest request,
	                                @AuthenticationPrincipal OrderAuthPrincipal principal){
		WebLaundryServiceUpdatePresenter presenter = new WebLaundryServiceUpdatePresenter();
		laundryServiceUpdateUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
