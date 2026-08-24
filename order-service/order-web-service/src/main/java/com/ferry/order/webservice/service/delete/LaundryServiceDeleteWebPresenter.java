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
public class LaundryServiceDeleteWebPresenter implements LaundryServiceDeletePresenter{
	private ResponseEntity<LaundryServiceDeleteWebResponse> responseEntity;

	@Override
	public void present(LaundryServiceDeleteResponse response){
		responseEntity = ResponseEntity.ok(new LaundryServiceDeleteWebResponse(response.serviceId()));
	}

}
