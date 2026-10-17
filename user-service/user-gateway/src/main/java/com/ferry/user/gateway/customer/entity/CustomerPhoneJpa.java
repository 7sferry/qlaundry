package com.ferry.user.gateway.customer.entity;

import com.ferry.user.domain.common.Phone;
import com.ferry.user.domain.customer.CustomerPhone;
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
@Table(name = CustomerPhoneJpa.TABLE,
		indexes = @Index(name = "idx_customer_phones_phone_hash", columnList = "phone_hash"))
public class CustomerPhoneJpa{
	static final String TABLE = "customer_phones";
	private static final String COLUMN_PHONE = "phone";

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Getter(AccessLevel.PRIVATE)
	private CustomerJpa customer;
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

	public void setCustomer(CustomerJpa customer){
		this.customer = customer;
		this.customerId = customer.getId();
	}

	private static CryptoAad aad(String customerId){
		return new CryptoAad(TABLE, COLUMN_PHONE, customerId);
	}

	public static CustomerPhoneJpa construct(String id, CustomerPhone register,
	                                               CustomerJpa customer, CryptoTool cryptoTool){
		CustomerPhoneJpa entity = new CustomerPhoneJpa();
		entity.setId(id);
		entity.setCustomer(customer);
		String phone = register.phone().value();
		entity.setPhoneCipher(cryptoTool.encrypt(phone, aad(customer.getId())));
		entity.setPhoneHash(cryptoTool.blindIndex(phone));
		entity.setCreatedBy(register.createdBy());
		entity.setCreatedAt(register.createdAt());
		entity.setUpdatedBy(register.updatedBy());
		entity.setUpdatedAt(register.updatedAt());
		entity.setDeleted(register.deleted());
		entity.setVersion(register.version());
		return entity;
	}

	public static CustomerPhone construct(CustomerPhoneJpa saved, CryptoTool cryptoTool){
		String phone = cryptoTool.decrypt(saved.getPhoneCipher(), aad(saved.getCustomerId()));
		return new CustomerPhone(saved.getId(), saved.getCustomerId(), new Phone(phone), saved.getVersion(),
				saved.isDeleted(), saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(), saved.getUpdatedBy());
	}

}
