package com.ferry.user.gateway.notification.entity;

import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.notification.EmailTriggerDomain;
import com.ferry.user.domain.notification.EmailTriggerStatus;
import com.ferry.user.domain.notification.EmailTriggerType;
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
@Table(name = EmailTriggerJpaEntity.TABLE)
public class EmailTriggerJpaEntity{
	static final String TABLE = "email_triggers";
	private static final String COLUMN_RECIPIENT = "recipient";
	private static final String COLUMN_PAYLOAD = "payload";

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private EmailTriggerTypeJpaEntity type;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "type_id", insertable = false, updatable = false)
	private short typeId;
	@Column(name = "recipient", nullable = false, length = 512)
	private String recipientCipher;
	@Column(name = "payload", nullable = false, columnDefinition = "text")
	private String payloadCipher;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private EmailTriggerStatusJpaEntity status;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "status_id", insertable = false, updatable = false)
	private short statusId;
	@Version
	private Integer version;
	@Column(nullable = false)
	private boolean deleted;
	@Column(nullable = false, length = 50, updatable = false)
	private String createdBy;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false, length = 50)
	private String updatedBy;
	@Column(nullable = false)
	private Instant updatedAt;

	private static CryptoAad recipientAad(String id){
		return new CryptoAad(TABLE, COLUMN_RECIPIENT, id);
	}

	private static CryptoAad payloadAad(String id){
		return new CryptoAad(TABLE, COLUMN_PAYLOAD, id);
	}

	public static EmailTriggerJpaEntity construct(String id, EmailTriggerDomain trigger, EmailTriggerTypeJpaEntity type,
	                                               EmailTriggerStatusJpaEntity status, CryptoTool cryptoTool){
		EmailTriggerJpaEntity entity = new EmailTriggerJpaEntity();
		entity.id = id;
		entity.type = type;
		entity.typeId = type.getId();
		entity.recipientCipher = cryptoTool.encrypt(trigger.recipientValue(), recipientAad(id));
		entity.payloadCipher = cryptoTool.encrypt(trigger.payload(), payloadAad(id));
		entity.status = status;
		entity.statusId = status.getId();
		entity.createdBy = trigger.createdBy();
		entity.createdAt = trigger.createdAt();
		entity.updatedBy = trigger.updatedBy();
		entity.updatedAt = trigger.updatedAt();
		entity.deleted = trigger.deleted();
		entity.version = trigger.version();
		return entity;
	}

	public static EmailTriggerDomain construct(EmailTriggerJpaEntity saved, CryptoTool cryptoTool){
		String recipient = cryptoTool.decrypt(saved.recipientCipher, recipientAad(saved.id));
		String payload = cryptoTool.decrypt(saved.payloadCipher, payloadAad(saved.id));
		return new EmailTriggerDomain(saved.id, EmailTriggerType.fromValue(saved.typeId).orElseThrow(),
				new EmailDomain(recipient), payload,
				EmailTriggerStatus.fromValue(saved.statusId).orElseThrow(), saved.version, saved.deleted,
				saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

	public void backfill(CryptoTool cryptoTool){
		String recipient = cryptoTool.decrypt(recipientCipher, recipientAad(id));
		if(recipient.equals(recipientCipher)){
			recipientCipher = cryptoTool.encrypt(recipient, recipientAad(id));
		}
		String payload = cryptoTool.decrypt(payloadCipher, payloadAad(id));
		if(payload.equals(payloadCipher)){
			payloadCipher = cryptoTool.encrypt(payload, payloadAad(id));
		}
	}

}
