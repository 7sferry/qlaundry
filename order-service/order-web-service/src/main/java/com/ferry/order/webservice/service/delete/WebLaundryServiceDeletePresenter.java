package com.ferry.order.webservice.service.delete;

import com.ferry.order.core.service.delete.LaundryServiceDeletePresenter;
import com.ferry.order.core.service.delete.LaundryServiceDeleteResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebLaundryServiceDeletePresenter implements LaundryServiceDeletePresenter{
	private ResponseEntity<LaundryServiceDeleteRestResponse> responseEntity;

	@Override
	public void present(LaundryServiceDeleteResponse response){
		responseEntity = ResponseEntity.ok(new LaundryServiceDeleteRestResponse(response.serviceId()));
	}

}
