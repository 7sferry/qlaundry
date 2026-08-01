package com.ferry.user.webservice.tenant.confirmregistration;

import com.ferry.user.core.tenant.confirmregistration.TenantConfirmRegistrationPresenter;
import com.ferry.user.core.tenant.confirmregistration.TenantConfirmRegistrationResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

@Getter
public class TenantConfirmRegistrationWebPresenter implements TenantConfirmRegistrationPresenter{
	private ResponseEntity<TenantConfirmRegistrationWebResponse> responseEntity;

	@Override
	public void present(TenantConfirmRegistrationResponse response){
		responseEntity = ResponseEntity.ok(new TenantConfirmRegistrationWebResponse(response.message()));
	}

}
