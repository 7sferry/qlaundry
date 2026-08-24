package com.ferry.order.webservice.service.create;

import com.ferry.order.core.service.create.LaundryServiceCreatePresenter;
import com.ferry.order.core.service.create.LaundryServiceCreateResponse;
import com.ferry.order.domain.service.LaundryServiceDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class LaundryServiceCreateWebPresenter implements LaundryServiceCreatePresenter{
	private ResponseEntity<LaundryServiceCreateWebResponse> responseEntity;

	@Override
	public void present(LaundryServiceCreateResponse response){
		LaundryServiceDomain service = response.service();
		responseEntity = ResponseEntity.ok(new LaundryServiceCreateWebResponse(service.id(), service.name(),
				service.descriptionValue(), service.pricePerUnit().value(), service.unit().name(),
				service.category().name(), service.estimatedHours(), service.expressMultiplier(), service.popular(),
				service.active()));
	}

}
