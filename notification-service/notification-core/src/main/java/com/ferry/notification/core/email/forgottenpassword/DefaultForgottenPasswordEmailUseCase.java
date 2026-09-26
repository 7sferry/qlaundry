package com.ferry.notification.core.email.forgottenpassword;

import com.ferry.notification.core.email.history.EmailHistoryGateway;
import com.ferry.notification.core.email.send.EmailSendGateway;
import com.ferry.notification.domain.Content;
import com.ferry.notification.domain.Email;
import com.ferry.notification.domain.EmailNotification;
import com.ferry.notification.domain.EmailType;
import com.ferry.notification.domain.Subject;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultForgottenPasswordEmailUseCase implements ForgottenPasswordEmailUseCase{
	private static final String SUBJECT = "QLaundry Password Reset Verification Code - ";

	private final ForgottenPasswordEmailComposer composer;
	private final EmailSendGateway emailSendGateway;
	private final EmailHistoryGateway emailHistoryGateway;

	@Override
	public void execute(ForgottenPasswordEmailRequest request, ForgottenPasswordEmailPresenter presenter){
		request.validate();
		Optional<Boolean> emailSent = emailHistoryGateway.findByReferenceId(request.triggerId())
				.filter(e -> e.sentAt() != null)
				.map(e -> {
					presenter.present(new ForgottenPasswordEmailResponse(e));
					return true;
				});
		if(emailSent.isPresent()){
			return;
		}
		Content content = new Content(composer.compose(request));
		EmailNotification notification = EmailNotification.compose(EmailType.FORGOTTEN_PASSWORD,
				request.triggerId(), new Email(request.recipient()),
				new Subject(SUBJECT + request.username()));
		emailSendGateway.send(notification, content);
		EmailNotification saved = emailHistoryGateway.save(notification.markSent());
		presenter.present(new ForgottenPasswordEmailResponse(saved));
	}

}
