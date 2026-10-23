package com.ferry.notification.core.email.passwordchanged;

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
 * on Oktober 2026      *
 ************************/

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultPasswordChangedEmailUseCase implements PasswordChangedEmailUseCase{
	private static final String SUBJECT = "QLaundry Password Changed - ";

	private final PasswordChangedEmailComposer composer;
	private final EmailSendGateway emailSendGateway;
	private final EmailHistoryGateway emailHistoryGateway;

	@Override
	public void execute(PasswordChangedEmailRequest request, PasswordChangedEmailPresenter presenter){
		request.validate();
		Optional<Boolean> emailSent = emailHistoryGateway.findByReferenceId(request.triggerId())
				.filter(e -> e.sentAt() != null)
				.map(e -> {
					presenter.present(new PasswordChangedEmailResponse(e));
					return true;
				});
		if(emailSent.isPresent()){
			return;
		}
		Content content = new Content(composer.compose(request));
		EmailNotification notification = EmailNotification.compose(EmailType.PASSWORD_CHANGED,
				request.triggerId(), new Email(request.recipient()),
				new Subject(SUBJECT + request.username()));
		emailSendGateway.send(notification, content);
		EmailNotification saved = emailHistoryGateway.save(notification.markSent());
		presenter.present(new PasswordChangedEmailResponse(saved));
	}

}
