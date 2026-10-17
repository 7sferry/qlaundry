package com.ferry.user.gateway.notification.entity;

import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.notification.EmailTrigger;
import com.ferry.user.domain.notification.EmailTriggerStatus;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.utils.crypto.CryptoAad;
import com.ferry.utils.crypto.CryptoTool;
import jakarta.persistence.*;
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
@Table(name = EmailTriggerJpa.TABLE)
public class EmailTriggerJpa{
	static final String TABLE = "email_triggers";
	private static final String COLUMN_RECIPIENT = "recipient";
	private static final String COLUMN_PAYLOAD = "payload";

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(nullable = false)
	private short typeId;
	@Column(name = "recipient", nullable = false, length = 512)
	private String recipientCipher;
	@Column(name = "payload", nullable = false, columnDefinition = "text")
	private String payloadCipher;
	@Column(nullable = false)
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

	public static EmailTriggerJpa construct(String id, EmailTrigger trigger, CryptoTool cryptoTool){
		EmailTriggerJpa entity = new EmailTriggerJpa();
		entity.setId(id);
		entity.setTypeId(trigger.typeIdValue());
		entity.setRecipientCipher(cryptoTool.encrypt(trigger.recipientValue(), recipientAad(id)));
		entity.setPayloadCipher(cryptoTool.encrypt(trigger.payload(), payloadAad(id)));
		entity.setStatusId(trigger.statusIdValue());
		entity.setCreatedBy(trigger.createdBy());
		entity.setCreatedAt(trigger.createdAt());
		entity.setUpdatedBy(trigger.updatedBy());
		entity.setUpdatedAt(trigger.updatedAt());
		entity.setDeleted(trigger.deleted());
		entity.setVersion(trigger.version());
		return entity;
	}

	public static EmailTrigger construct(EmailTriggerJpa saved, CryptoTool cryptoTool){
		String recipient = cryptoTool.decrypt(saved.getRecipientCipher(), recipientAad(saved.getId()));
		String payload = cryptoTool.decrypt(saved.getPayloadCipher(), payloadAad(saved.getId()));
		return new EmailTrigger(saved.getId(), EmailTriggerType.fromValue(saved.getTypeId()).orElseThrow(),
				new Email(recipient), payload,
				EmailTriggerStatus.fromValue(saved.getStatusId()).orElseThrow(), saved.getVersion(), saved.isDeleted(),
				saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(), saved.getUpdatedBy());
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
