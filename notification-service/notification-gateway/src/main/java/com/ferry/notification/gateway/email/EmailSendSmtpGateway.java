package com.ferry.notification.gateway.email;

import com.ferry.notification.core.email.send.EmailSendGateway;
import com.ferry.notification.domain.ContentDomain;
import com.ferry.notification.domain.EmailNotificationDomain;
import com.ferry.notification.domain.exception.NotificationDeliveryException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.charset.StandardCharsets;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class EmailSendSmtpGateway implements EmailSendGateway{
	private final JavaMailSender mailSender;
	private final String senderAddress;

	@Override
	public void send(EmailNotificationDomain notification, ContentDomain content){
		try{
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
			helper.setFrom(senderAddress);
			helper.setTo(notification.recipientValue());
			helper.setSubject(notification.subjectValue());
			helper.setText(content.value(), true);
			mailSender.send(message);
		}catch(MessagingException | MailException e){
			throw new NotificationDeliveryException("Failed to send email notification", e);
		}
	}

}
