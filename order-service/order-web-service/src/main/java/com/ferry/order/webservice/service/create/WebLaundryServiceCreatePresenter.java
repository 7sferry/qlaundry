package com.ferry.order.webservice.service.create;

import com.ferry.order.core.service.create.LaundryServiceCreatePresenter;
import com.ferry.order.core.service.create.LaundryServiceCreateResponse;
import com.ferry.order.domain.service.LaundryService;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebLaundryServiceCreatePresenter implements LaundryServiceCreatePresenter{
	private ResponseEntity<LaundryServiceCreateRestResponse> responseEntity;

	@Override
	public void present(LaundryServiceCreateResponse response){
		LaundryService service = response.service();
		responseEntity = ResponseEntity.ok(new LaundryServiceCreateRestResponse(service.id(), service.name(),
				service.descriptionValue(), service.pricePerUnit().value(), service.unit().name(),
				service.category().name(), service.estimatedHours(), service.expressMultiplier(), service.popular(),
				service.active()));
	}

}
