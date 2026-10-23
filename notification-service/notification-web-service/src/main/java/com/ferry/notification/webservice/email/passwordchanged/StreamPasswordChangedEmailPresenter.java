package com.ferry.notification.webservice.email.passwordchanged;

import com.ferry.notification.core.email.passwordchanged.PasswordChangedEmailPresenter;
import com.ferry.notification.core.email.passwordchanged.PasswordChangedEmailResponse;
import com.ferry.notification.domain.EmailNotification;
import lombok.Getter;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class StreamPasswordChangedEmailPresenter implements PasswordChangedEmailPresenter{
	private EmailNotification notification;

	@Override
	public void present(PasswordChangedEmailResponse response){
		notification = response.notification();
	}
}
