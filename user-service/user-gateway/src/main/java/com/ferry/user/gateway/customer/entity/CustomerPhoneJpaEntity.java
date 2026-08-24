package com.ferry.user.gateway.customer.entity;

import com.ferry.user.domain.common.PhoneDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
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
 * on Agustus 2026      *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = CustomerPhoneJpaEntity.TABLE,
		indexes = @Index(name = "idx_customer_phones_phone_hash", columnList = "phone_hash"))
public class CustomerPhoneJpaEntity{
	static final String TABLE = "customer_phones";
	private static final String COLUMN_PHONE = "phone";

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private CustomerJpaEntity customer;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "customer_id", insertable = false, updatable = false)
	private String customerId;
	@Column(name = "phone", nullable = false, length = 128)
	private String phoneCipher;
	@Column(name = "phone_hash", length = 64)
	private String phoneHash;
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
		return new CryptoAad(TABLE, COLUMN_PHONE, customerId);
	}

	public static CustomerPhoneJpaEntity construct(String id, CustomerPhoneDomain register,
	                                               CustomerJpaEntity customer, CryptoTool cryptoTool){
		CustomerPhoneJpaEntity entity = new CustomerPhoneJpaEntity();
		entity.id = id;
		entity.customer = customer;
		entity.customerId = customer.getId();
		String phone = register.phone().value();
		entity.phoneCipher = cryptoTool.encrypt(phone, aad(customer.getId()));
		entity.phoneHash = cryptoTool.blindIndex(phone);
		entity.createdBy = register.createdBy();
		entity.createdAt = register.createdAt();
		entity.updatedBy = register.updatedBy();
		entity.updatedAt = register.updatedAt();
		entity.deleted = register.deleted();
		entity.version = register.version();
		return entity;
	}

	public static CustomerPhoneDomain construct(CustomerPhoneJpaEntity saved, CryptoTool cryptoTool){
		String phone = cryptoTool.decrypt(saved.phoneCipher, aad(saved.customerId));
		return new CustomerPhoneDomain(saved.id, saved.customerId, new PhoneDomain(phone), saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

}
