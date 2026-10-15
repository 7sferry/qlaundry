package com.ferry.analytics.webservice.service;

import com.ferry.analytics.core.service.ServiceSummaryPresenter;
import com.ferry.analytics.core.service.ServiceSummaryResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebServiceSummaryPresenter implements ServiceSummaryPresenter{
	private ResponseEntity<ServiceSummaryRestResponse> responseEntity;

	@Override
	public void present(ServiceSummaryResponse response){
		responseEntity = ResponseEntity.ok(new ServiceSummaryRestResponse(response.totalServices(),
				response.activeServices(), response.popularServices(), response.averagePrice()));
	}

}
