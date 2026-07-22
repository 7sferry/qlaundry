package com.ferry.notification.gateway.email;

import com.ferry.notification.core.email.tenantregistration.TenantRegistrationEmailComposer;
import com.ferry.notification.core.email.tenantregistration.TenantRegistrationEmailRequest;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class TenantRegistrationEmailThymeleafComposer implements TenantRegistrationEmailComposer{
	private static final String TEMPLATE_NAME = "tenant-registration";
	private static final Locale LOCALE = Locale.of("id", "ID");
	private static final DateTimeFormatter REGISTERED_AT_FORMATTER = DateTimeFormatter
			.ofPattern("d MMMM yyyy, HH:mm 'WIB'", LOCALE)
			.withZone(ZoneId.of("Asia/Jakarta"));

	private final ITemplateEngine templateEngine;

	@Override
	public String compose(TenantRegistrationEmailRequest request){
		Context context = new Context(LOCALE);
		context.setVariable("staffFullName", request.staffFullName());
		context.setVariable("staffUsername", request.staffUsername());
		context.setVariable("tenantName", request.tenantName());
		context.setVariable("tenantDescription", request.tenantDescription());
		context.setVariable("registeredAt", REGISTERED_AT_FORMATTER.format(request.registeredAt()));
		return templateEngine.process(TEMPLATE_NAME, context);
	}

}
