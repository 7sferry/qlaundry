package com.ferry.notification.webservice.email.forgottenpassword;

import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailPresenter;
import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailResponse;
import com.ferry.notification.domain.EmailNotification;
import lombok.Getter;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
public class StreamForgottenPasswordEmailPresenter implements ForgottenPasswordEmailPresenter{
	private EmailNotification notification;

	@Override
	public void present(ForgottenPasswordEmailResponse response){
		notification = response.notification();
	}
}
