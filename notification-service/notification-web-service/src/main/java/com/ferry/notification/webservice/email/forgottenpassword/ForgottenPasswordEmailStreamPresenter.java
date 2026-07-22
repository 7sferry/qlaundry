package com.ferry.notification.webservice.email.forgottenpassword;

import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailPresenter;
import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailResponse;
import com.ferry.notification.domain.EmailNotificationDomain;
import lombok.Getter;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class ForgottenPasswordEmailStreamPresenter implements ForgottenPasswordEmailPresenter{
	private EmailNotificationDomain notification;

	@Override
	public void present(ForgottenPasswordEmailResponse response){
		notification = response.notification();
	}
}
