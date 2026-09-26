package com.ferry.notification.core.email.tenantregistration;

import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailResponse;
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
public class DefaultTenantRegistrationEmailUseCase implements TenantRegistrationEmailUseCase{
	private static final String SUBJECT_PREFIX = "Confirm your QLaundry registration - ";

	private final TenantRegistrationEmailComposer composer;
	private final EmailSendGateway emailSendGateway;
	private final EmailHistoryGateway emailHistoryGateway;

	@Override
	public void execute(TenantRegistrationEmailRequest request, TenantRegistrationEmailPresenter presenter){
		request.validate();
		Optional<Boolean> emailSent = emailHistoryGateway.findByReferenceId(request.triggerId())
				.filter(e -> e.sentAt() != null)
				.map(e -> {
					presenter.present(new TenantRegistrationEmailResponse(e));
					return true;
				});
		if(emailSent.isPresent()){
			return;
		}
		Content content = new Content(composer.compose(request));
		EmailNotification notification = EmailNotification.compose(EmailType.TENANT_REGISTRATION,
				request.triggerId(), new Email(request.recipient()),
				new Subject(SUBJECT_PREFIX + request.tenantName()));
		emailSendGateway.send(notification, content);
		EmailNotification saved = emailHistoryGateway.save(notification.markSent());
		presenter.present(new TenantRegistrationEmailResponse(saved));
	}

}
