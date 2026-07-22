package com.ferry.notification.core.email.forgottenpassword;

import com.ferry.notification.core.email.history.EmailHistoryGateway;
import com.ferry.notification.core.email.send.EmailSendGateway;
import com.ferry.notification.domain.ContentDomain;
import com.ferry.notification.domain.EmailDomain;
import com.ferry.notification.domain.EmailNotificationDomain;
import com.ferry.notification.domain.EmailType;
import com.ferry.notification.domain.SubjectDomain;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultForgottenPasswordEmailUseCase implements ForgottenPasswordEmailUseCase{
	private static final String SUBJECT = "Kode Verifikasi Reset Password QLaundry";

	private final ForgottenPasswordEmailComposer composer;
	private final EmailSendGateway emailSendGateway;
	private final EmailHistoryGateway emailHistoryGateway;

	@Override
	public void execute(ForgottenPasswordEmailRequest request, ForgottenPasswordEmailPresenter presenter){
		String content = composer.compose(request);
		EmailNotificationDomain notification = EmailNotificationDomain.compose(EmailType.FORGOTTEN_PASSWORD,
				request.triggerId(), new EmailDomain(request.recipient()), new SubjectDomain(SUBJECT),
				new ContentDomain(content));
		emailSendGateway.send(notification);
		EmailNotificationDomain saved = emailHistoryGateway.save(notification.markSent());
		presenter.present(new ForgottenPasswordEmailResponse(saved));
	}

}
