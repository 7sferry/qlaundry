package com.ferry.user.gateway.customer.entity;

import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
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
 * on Agustus 2026      *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = CustomerEmailJpaEntity.TABLE,
		indexes = @Index(name = "idx_customer_emails_email_hash", columnList = "email_hash"))
public class CustomerEmailJpaEntity{
	static final String TABLE = "customer_emails";
	private static final String COLUMN_EMAIL = "email";

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private CustomerJpaEntity customer;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "customer_id", insertable = false, updatable = false)
	private String customerId;
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

	public void setCustomer(CustomerJpaEntity customer){
		this.customer = customer;
		this.customerId = customer.getId();
	}

	private static CryptoAad aad(String customerId){
		return new CryptoAad(TABLE, COLUMN_EMAIL, customerId);
	}

	public static String normalize(String email){
		return email.toLowerCase(Locale.ROOT).trim();
	}

	public static CustomerEmailJpaEntity construct(String id, CustomerEmailDomain register,
	                                               CustomerJpaEntity customer, CryptoTool cryptoTool){
		CustomerEmailJpaEntity entity = new CustomerEmailJpaEntity();
		entity.id = id;
		entity.customer = customer;
		entity.customerId = customer.getId();
		String email = register.email().value();
		entity.emailCipher = cryptoTool.encrypt(email, aad(customer.getId()));
		entity.emailHash = cryptoTool.blindIndex(normalize(email));
		entity.createdBy = register.createdBy();
		entity.createdAt = register.createdAt();
		entity.updatedBy = register.updatedBy();
		entity.updatedAt = register.updatedAt();
		entity.deleted = register.deleted();
		entity.version = register.version();
		return entity;
	}

	public static CustomerEmailDomain construct(CustomerEmailJpaEntity saved, CryptoTool cryptoTool){
		String email = cryptoTool.decrypt(saved.emailCipher, aad(saved.customerId));
		return new CustomerEmailDomain(saved.id, saved.customerId, new EmailDomain(email), saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

}
