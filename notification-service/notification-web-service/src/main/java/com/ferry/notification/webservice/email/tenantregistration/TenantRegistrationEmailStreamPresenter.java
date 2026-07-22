package com.ferry.notification.webservice.email.tenantregistration;

import com.ferry.notification.core.email.tenantregistration.TenantRegistrationEmailPresenter;
import com.ferry.notification.core.email.tenantregistration.TenantRegistrationEmailResponse;
import com.ferry.notification.domain.EmailNotificationDomain;
import lombok.Getter;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class TenantRegistrationEmailStreamPresenter implements TenantRegistrationEmailPresenter{
	private EmailNotificationDomain notification;

	@Override
	public void present(TenantRegistrationEmailResponse response){
		notification = response.notification();
	}
}
