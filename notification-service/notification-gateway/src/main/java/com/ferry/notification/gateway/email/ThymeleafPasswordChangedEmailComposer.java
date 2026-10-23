package com.ferry.notification.gateway.email;

import com.ferry.notification.core.email.passwordchanged.PasswordChangedEmailComposer;
import com.ferry.notification.core.email.passwordchanged.PasswordChangedEmailRequest;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class ThymeleafPasswordChangedEmailComposer implements PasswordChangedEmailComposer{
	private static final String TEMPLATE_NAME = "password-changed";
	private static final Locale LOCALE = Locale.of("id", "ID");
	private static final DateTimeFormatter CHANGED_AT_FORMATTER = DateTimeFormatter
			.ofPattern("d MMMM yyyy, HH:mm 'WIB'", LOCALE)
			.withZone(ZoneId.of("Asia/Jakarta"));

	private final ITemplateEngine templateEngine;

	@Override
	public String compose(PasswordChangedEmailRequest request){
		Context context = new Context(LOCALE);
		context.setVariable("username", request.username());
		context.setVariable("changedAt", CHANGED_AT_FORMATTER.format(request.changedAt()));
		return templateEngine.process(TEMPLATE_NAME, context);
	}

}
