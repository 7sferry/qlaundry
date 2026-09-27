package com.ferry.user.webservice.tenant.update;

import com.ferry.user.core.tenant.update.TenantUpdatePresenter;
import com.ferry.user.core.tenant.update.TenantUpdateResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

@Getter
public class WebTenantUpdatePresenter implements TenantUpdatePresenter{
	private ResponseEntity<TenantUpdateRestResponse> responseEntity;

	@Override
	public void present(TenantUpdateResponse response){
		responseEntity = ResponseEntity.ok(new TenantUpdateRestResponse(response.tenant().fullNameValue(),
				response.tenant().descriptionValue(), response.tenant().timeZone().getId()));
	}

}
