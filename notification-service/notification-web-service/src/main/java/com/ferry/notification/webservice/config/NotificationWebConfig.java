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
import com.ferry.notification.gateway.email.entity.EmailNotificationJpaEntity;
import com.ferry.notification.gateway.email.repository.EmailNotificationJpaRepository;
import com.ferry.notification.gateway.email.repository.EmailTypeJpaRepository;
import com.ferry.utils.crypto.AesGcmCryptoTool;
import com.ferry.utils.crypto.CryptoKeyConfig;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.generator.UlidGenerator;
import com.ferry.utils.json.DefaultJsonManager;
import com.ferry.utils.json.JsonManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Slf4j
@Configuration
@EnableConfigurationProperties(CryptoKeysProperties.class)
public class NotificationWebConfig{

	@Bean
	IdGenerator idGenerator(){
		return new UlidGenerator();
	}

	@Bean
	CryptoTool cryptoTool(CryptoKeysProperties cryptoKeysProperties){
		return new AesGcmCryptoTool(CryptoKeyConfig.of(cryptoKeysProperties.activeKeyId(),
				cryptoKeysProperties.keys(), cryptoKeysProperties.blindIndexKey(),
				cryptoKeysProperties.allowPlaintextRead()));
	}

	// one-off encryption backfill: run once with --spring.profiles.active=backfill while
	// app.crypto.allow-plaintext-read is true, then flip it to false
//	@Bean
	ApplicationRunner cryptoBackfillRunner(EmailNotificationJpaRepository emailNotificationJpaRepository,
	                                       CryptoTool cryptoTool){
		return _ -> {
			List<EmailNotificationJpaEntity> notifications = emailNotificationJpaRepository.findAll();
			notifications.forEach(entity -> entity.backfill(cryptoTool));
			emailNotificationJpaRepository.saveAll(notifications);
			log.info("Crypto backfill done: {} email notifications", notifications.size());
		};
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
	TenantRegistrationEmailComposer tenantRegistrationEmailComposer(ITemplateEngine emailTemplateEngine,
	                                                                @Value("${app.notification.confirmation.base-url}") String confirmationBaseUrl){
		return new TenantRegistrationEmailThymeleafComposer(emailTemplateEngine, confirmationBaseUrl);
	}

	@Bean
	EmailSendGateway emailSendGateway(JavaMailSender javaMailSender,
	                                  @Value("${app.mail.sender}") String senderAddress){
		return new EmailSendSmtpGateway(javaMailSender, senderAddress);
	}

	@Bean
	EmailHistoryGateway emailHistoryGateway(EmailNotificationJpaRepository emailNotificationJpaRepository,
	                                        EmailTypeJpaRepository emailTypeJpaRepository,
	                                        IdGenerator idGenerator,
	                                        CryptoTool cryptoTool){
		return new EmailHistoryJpaGateway(emailNotificationJpaRepository, emailTypeJpaRepository, idGenerator, cryptoTool);
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
