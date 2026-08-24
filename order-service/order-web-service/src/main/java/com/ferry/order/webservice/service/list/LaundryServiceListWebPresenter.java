package com.ferry.order.webservice.service.list;

import com.ferry.order.core.service.list.LaundryServiceListPresenter;
import com.ferry.order.core.service.list.LaundryServiceListResponse;
import com.ferry.order.webservice.service.list.LaundryServiceListWebResponse.Service;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class LaundryServiceListWebPresenter implements LaundryServiceListPresenter{
	private ResponseEntity<LaundryServiceListWebResponse> responseEntity;

	@Override
	public void present(LaundryServiceListResponse response){
		List<Service> services = response.services().stream()
				.map(o -> new Service(o.id(), o.name(), o.descriptionValue(), o.pricePerUnit().value(),
						o.unit().name(), o.category().name(), o.estimatedHours(), o.expressMultiplier(), o.popular(),
						o.active()))
				.toList();
		responseEntity = ResponseEntity.ok(new LaundryServiceListWebResponse(services));
	}

}
