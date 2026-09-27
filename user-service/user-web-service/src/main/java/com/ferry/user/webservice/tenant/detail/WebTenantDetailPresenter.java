package com.ferry.user.webservice.tenant.detail;

import com.ferry.user.core.tenant.detail.TenantDetailPresenter;
import com.ferry.user.core.tenant.detail.TenantDetailResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

@Getter
public class WebTenantDetailPresenter implements TenantDetailPresenter{
	private ResponseEntity<TenantDetailRestResponse> responseEntity;

	@Override
	public void present(TenantDetailResponse response){
		responseEntity = ResponseEntity.ok(new TenantDetailRestResponse(response.tenant().fullNameValue(),
				response.tenant().descriptionValue(), response.tenant().timeZone().getId()));
	}

}
