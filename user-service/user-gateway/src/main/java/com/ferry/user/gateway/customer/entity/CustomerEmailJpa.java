package com.ferry.user.gateway.customer.entity;

import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.customer.CustomerEmail;
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
@Table(name = CustomerEmailJpa.TABLE,
		indexes = @Index(name = "idx_customer_emails_email_hash", columnList = "email_hash"))
public class CustomerEmailJpa{
	static final String TABLE = "customer_emails";
	private static final String COLUMN_EMAIL = "email";

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Getter(AccessLevel.PRIVATE)
	private CustomerJpa customer;
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

	public void setCustomer(CustomerJpa customer){
		this.customer = customer;
		this.customerId = customer.getId();
	}

	private static CryptoAad aad(String customerId){
		return new CryptoAad(TABLE, COLUMN_EMAIL, customerId);
	}

	public static String normalize(String email){
		return email.toLowerCase(Locale.ROOT).trim();
	}

	public static CustomerEmailJpa construct(String id, CustomerEmail register,
	                                               CustomerJpa customer, CryptoTool cryptoTool){
		CustomerEmailJpa entity = new CustomerEmailJpa();
		entity.setId(id);
		entity.setCustomer(customer);
		String email = register.email().value();
		entity.setEmailCipher(cryptoTool.encrypt(email, aad(customer.getId())));
		entity.setEmailHash(cryptoTool.blindIndex(normalize(email)));
		entity.setCreatedBy(register.createdBy());
		entity.setCreatedAt(register.createdAt());
		entity.setUpdatedBy(register.updatedBy());
		entity.setUpdatedAt(register.updatedAt());
		entity.setDeleted(register.deleted());
		entity.setVersion(register.version());
		return entity;
	}

	public static CustomerEmail construct(CustomerEmailJpa saved, CryptoTool cryptoTool){
		String email = cryptoTool.decrypt(saved.getEmailCipher(), aad(saved.getCustomerId()));
		return new CustomerEmail(saved.getId(), saved.getCustomerId(), new Email(email), saved.getVersion(),
				saved.isDeleted(), saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(), saved.getUpdatedBy());
	}

}
