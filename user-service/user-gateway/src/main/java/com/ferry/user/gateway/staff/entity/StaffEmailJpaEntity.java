package com.ferry.user.gateway.staff.entity;

import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
import com.ferry.utils.crypto.CryptoAad;
import com.ferry.utils.crypto.CryptoTool;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Locale;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = StaffEmailJpaEntity.TABLE, indexes = @Index(name = "idx_staff_emails_email_hash", columnList = "email_hash"))
public class StaffEmailJpaEntity{
	static final String TABLE = "staff_emails";
	private static final String COLUMN_EMAIL = "email";

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private StaffJpaEntity staff;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "staff_id", insertable = false, updatable = false)
	private String staffId;
	@Column(name = "email", nullable = false, length = 512)
	private String emailCipher;
	@Column(name = "email_hash", length = 64)
	private String emailHash;
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

	public void setStaff(StaffJpaEntity staff){
		this.staff = staff;
		this.staffId = staff.getId();
	}

	private static CryptoAad aad(String staffId){
		return new CryptoAad(TABLE, COLUMN_EMAIL, staffId);
	}

	public static String normalize(String email){
		return email.toLowerCase(Locale.ROOT).trim();
	}

	public static StaffEmailJpaEntity construct(String id, StaffEmailDomain register, StaffJpaEntity staff,
	                                            CryptoTool cryptoTool){
		StaffEmailJpaEntity entity = new StaffEmailJpaEntity();
		entity.id = id;
		entity.staffId = staff.getId();
		entity.staff = staff;
		String email = register.email().value();
		entity.emailCipher = cryptoTool.encrypt(email, aad(staff.getId()));
		entity.emailHash = cryptoTool.blindIndex(normalize(email));
		entity.createdBy = register.createdBy();
		entity.updatedAt = register.updatedAt();
		entity.createdAt = register.createdAt();
		entity.updatedBy = register.updatedBy();
		entity.deleted = register.deleted();
		entity.version = register.version();
		return entity;
	}

	public static StaffEmailDomain construct(StaffEmailJpaEntity saved, CryptoTool cryptoTool){
		String email = cryptoTool.decrypt(saved.emailCipher, aad(saved.staffId));
		return new StaffEmailDomain(saved.id, saved.staffId, new EmailDomain(email), saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

	public static String decryptEmail(String emailCipher, String staffId, CryptoTool cryptoTool){
		return cryptoTool.decrypt(emailCipher, aad(staffId));
	}

	public void backfill(CryptoTool cryptoTool){
		String email = cryptoTool.decrypt(emailCipher, aad(staffId));
		if(email.equals(emailCipher)){
			emailCipher = cryptoTool.encrypt(email, aad(staffId));
		}
		emailHash = cryptoTool.blindIndex(normalize(email));
	}
}
