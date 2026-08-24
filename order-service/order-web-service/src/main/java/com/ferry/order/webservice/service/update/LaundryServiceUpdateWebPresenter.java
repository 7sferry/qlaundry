package com.ferry.order.webservice.service.update;

import com.ferry.order.core.service.update.LaundryServiceUpdatePresenter;
import com.ferry.order.core.service.update.LaundryServiceUpdateResponse;
import com.ferry.order.domain.service.LaundryServiceDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class LaundryServiceUpdateWebPresenter implements LaundryServiceUpdatePresenter{
	private ResponseEntity<LaundryServiceUpdateWebResponse> responseEntity;

	@Override
	public void present(LaundryServiceUpdateResponse response){
		LaundryServiceDomain service = response.service();
		responseEntity = ResponseEntity.ok(new LaundryServiceUpdateWebResponse(service.id(), service.name(),
				service.descriptionValue(), service.pricePerUnit().value(), service.unit().name(),
				service.category().name(), service.estimatedHours(), service.expressMultiplier(), service.popular(),
				service.active()));
	}

}
