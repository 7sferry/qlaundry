package com.ferry.notification.gateway.email;

import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailComposer;
import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailRequest;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class ForgottenPasswordEmailThymeleafComposer implements ForgottenPasswordEmailComposer{
	private static final String TEMPLATE_NAME = "forgotten-password";
	private static final Locale LOCALE = Locale.of("id", "ID");

	private final ITemplateEngine templateEngine;

	@Override
	public String compose(ForgottenPasswordEmailRequest request){
		Context context = new Context(LOCALE);
		context.setVariable("username", request.username());
		context.setVariable("otp", request.otp());
		return templateEngine.process(TEMPLATE_NAME, context);
	}

}
