package com.ferry.notification.gateway.email.entity;

import com.ferry.notification.domain.EmailNotificationDomain;
import com.ferry.utils.crypto.CryptoAad;
import com.ferry.utils.crypto.CryptoTool;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = EmailNotificationJpaEntity.TABLE)
public class EmailNotificationJpaEntity{
	static final String TABLE = "email_notifications";
	private static final String COLUMN_RECIPIENT = "recipient";

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(length = 50, unique = true)
	private String referenceId;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private EmailTypeJpaEntity type;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "type_id", insertable = false, updatable = false)
	private short typeId;
	@Column(name = "recipient", nullable = false, length = 512)
	private String recipientCipher;
	@Column(nullable = false, length = 200)
	private String subject;
	@Version
	private Integer version;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false)
	private Instant sentAt;

	private static CryptoAad recipientAad(String id){
		return new CryptoAad(TABLE, COLUMN_RECIPIENT, id);
	}

	public static EmailNotificationJpaEntity construct(String id, EmailNotificationDomain notification,
	                                                    EmailTypeJpaEntity type, CryptoTool cryptoTool){
		EmailNotificationJpaEntity entity = new EmailNotificationJpaEntity();
		entity.id = id;
		entity.referenceId = notification.referenceId();
		entity.type = type;
		entity.typeId = type.getId();
		entity.recipientCipher = cryptoTool.encrypt(notification.recipientValue(), recipientAad(id));
		entity.subject = notification.subjectValue();
		entity.createdAt = notification.createdAt();
		entity.sentAt = notification.sentAt();
		entity.version = notification.version();
		return entity;
	}

	public String decryptRecipient(CryptoTool cryptoTool){
		return cryptoTool.decrypt(recipientCipher, recipientAad(id));
	}

	public void backfill(CryptoTool cryptoTool){
		String recipient = cryptoTool.decrypt(recipientCipher, recipientAad(id));
		if(recipient.equals(recipientCipher)){
			recipientCipher = cryptoTool.encrypt(recipient, recipientAad(id));
		}
	}

}
