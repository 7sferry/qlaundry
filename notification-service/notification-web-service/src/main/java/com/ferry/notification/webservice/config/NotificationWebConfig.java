package com.ferry.notification.webservice.config;

import com.ferry.notification.core.email.forgottenpassword.DefaultForgottenPasswordEmailUseCase;
import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailComposer;
import com.ferry.notification.core.email.forgottenpassword.ForgottenPasswordEmailUseCase;
import com.ferry.notification.core.email.history.EmailHistoryGateway;
import com.ferry.notification.core.email.send.EmailSendGateway;
import com.ferry.notification.core.email.tenantregistration.DefaultTenantRegistrationEmailUseCase;
import com.ferry.notification.core.email.tenantregistration.TenantRegistrationEmailComposer;
import com.ferry.notification.core.email.tenantregistration.TenantRegistrationEmailUseCase;
import com.ferry.notification.gateway.email.EmailHistoryJpaGateway;
import com.ferry.notification.gateway.email.EmailSendSmtpGateway;
import com.ferry.notification.gateway.email.ForgottenPasswordEmailThymeleafComposer;
import com.ferry.notification.gateway.email.TenantRegistrationEmailThymeleafComposer;
import com.ferry.notification.gateway.email.repository.EmailNotificationJpaRepository;
import com.ferry.notification.gateway.email.repository.EmailTypeJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.generator.UlidGenerator;
import com.ferry.utils.json.DefaultJsonManager;
import com.ferry.utils.json.JsonManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Configuration
public class NotificationWebConfig{

	@Bean
	IdGenerator idGenerator(){
		return new UlidGenerator();
	}

	@Bean
	JsonManager jsonManager(ObjectMapper objectMapper){
		return new DefaultJsonManager(objectMapper);
	}

	@Bean
	ITemplateEngine emailTemplateEngine(){
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("templates/mail/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		templateResolver.setCacheable(true);
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		return templateEngine;
	}

	@Bean
	TenantRegistrationEmailComposer tenantRegistrationEmailComposer(ITemplateEngine emailTemplateEngine){
		return new TenantRegistrationEmailThymeleafComposer(emailTemplateEngine);
	}

	@Bean
	EmailSendGateway emailSendGateway(JavaMailSender javaMailSender,
	                                  @Value("${app.mail.sender}") String senderAddress){
		return new EmailSendSmtpGateway(javaMailSender, senderAddress);
	}

	@Bean
	EmailHistoryGateway emailHistoryGateway(EmailNotificationJpaRepository emailNotificationJpaRepository,
	                                        EmailTypeJpaRepository emailTypeJpaRepository,
	                                        IdGenerator idGenerator){
		return new EmailHistoryJpaGateway(emailNotificationJpaRepository, emailTypeJpaRepository, idGenerator);
	}

	@Bean
	TenantRegistrationEmailUseCase tenantRegistrationEmailUseCase(TenantRegistrationEmailComposer tenantRegistrationEmailComposer,
	                                                              EmailSendGateway emailSendGateway,
	                                                              EmailHistoryGateway emailHistoryGateway){
		return new DefaultTenantRegistrationEmailUseCase(tenantRegistrationEmailComposer, emailSendGateway, emailHistoryGateway);
	}

	@Bean
	ForgottenPasswordEmailComposer forgottenPasswordEmailComposer(ITemplateEngine emailTemplateEngine){
		return new ForgottenPasswordEmailThymeleafComposer(emailTemplateEngine);
	}

	@Bean
	ForgottenPasswordEmailUseCase forgottenPasswordEmailUseCase(ForgottenPasswordEmailComposer forgottenPasswordEmailComposer,
	                                                            EmailSendGateway emailSendGateway,
	                                                            EmailHistoryGateway emailHistoryGateway){
		return new DefaultForgottenPasswordEmailUseCase(forgottenPasswordEmailComposer, emailSendGateway, emailHistoryGateway);
	}

}
