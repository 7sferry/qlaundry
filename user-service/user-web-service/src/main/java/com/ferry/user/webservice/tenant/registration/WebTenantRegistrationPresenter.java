package com.ferry.user.webservice.tenant.registration;

import com.ferry.user.core.tenant.registration.TenantRegistrationPresenter;
import com.ferry.user.core.tenant.registration.TenantRegistrationResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class WebTenantRegistrationPresenter implements TenantRegistrationPresenter{
	private ResponseEntity<TenantRegistrationRestResponse> responseEntity;

	@Override
	public void present(TenantRegistrationResponse response){
		TenantRegistrationRestResponse body = new TenantRegistrationRestResponse(response.tenantName(), response.staffUserName());
		responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(body);
	}
}
