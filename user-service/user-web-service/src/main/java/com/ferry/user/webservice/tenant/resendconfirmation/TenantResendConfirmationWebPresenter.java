package com.ferry.user.webservice.tenant.resendconfirmation;

import com.ferry.user.core.tenant.resendconfirmation.TenantResendConfirmationPresenter;
import com.ferry.user.core.tenant.resendconfirmation.TenantResendConfirmationResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

@Getter
public class TenantResendConfirmationWebPresenter implements TenantResendConfirmationPresenter{
	private ResponseEntity<TenantResendConfirmationWebResponse> responseEntity;

	@Override
	public void present(TenantResendConfirmationResponse response){
		responseEntity = ResponseEntity.ok(new TenantResendConfirmationWebResponse(response.message()));
	}

}
