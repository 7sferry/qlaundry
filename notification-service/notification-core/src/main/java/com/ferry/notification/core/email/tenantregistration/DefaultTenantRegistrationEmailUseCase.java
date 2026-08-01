package com.ferry.notification.core.email.tenantregistration;

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
public class DefaultTenantRegistrationEmailUseCase implements TenantRegistrationEmailUseCase{
	private static final String SUBJECT_PREFIX = "Confirm your QLaundry registration - ";

	private final TenantRegistrationEmailComposer composer;
	private final EmailSendGateway emailSendGateway;
	private final EmailHistoryGateway emailHistoryGateway;

	@Override
	public void execute(TenantRegistrationEmailRequest request, TenantRegistrationEmailPresenter presenter){
		request.validate();
		ContentDomain content = new ContentDomain(composer.compose(request));
		EmailNotificationDomain notification = EmailNotificationDomain.compose(EmailType.TENANT_REGISTRATION,
				request.triggerId(), new EmailDomain(request.recipient()),
				new SubjectDomain(SUBJECT_PREFIX + request.tenantName()));
		emailSendGateway.send(notification, content);
		EmailNotificationDomain saved = emailHistoryGateway.save(notification.markSent());
		presenter.present(new TenantRegistrationEmailResponse(saved));
	}

}
