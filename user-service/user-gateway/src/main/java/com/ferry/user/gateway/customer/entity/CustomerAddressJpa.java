package com.ferry.user.gateway.customer.entity;

import com.ferry.user.domain.common.AddressLine;
import com.ferry.user.domain.customer.CustomerAddress;
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
@Table(name = CustomerAddressJpa.TABLE)
public class CustomerAddressJpa{
	static final String TABLE = "customer_addresses";
	private static final String COLUMN_ADDRESS_LINE = "address_line";

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Getter(AccessLevel.PRIVATE)
	private CustomerJpa customer;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "customer_id", insertable = false, updatable = false)
	private String customerId;
	@Column(name = "address_line", nullable = false, length = 1024)
	private String addressLineCipher;
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
		return new CryptoAad(TABLE, COLUMN_ADDRESS_LINE, customerId);
	}

	public static CustomerAddressJpa construct(String id, CustomerAddress register,
	                                                 CustomerJpa customer, CryptoTool cryptoTool){
		CustomerAddressJpa entity = new CustomerAddressJpa();
		entity.id = id;
		entity.customer = customer;
		entity.customerId = customer.getId();
		entity.addressLineCipher = cryptoTool.encrypt(register.addressLine().value(), aad(customer.getId()));
		entity.createdBy = register.createdBy();
		entity.createdAt = register.createdAt();
		entity.updatedBy = register.updatedBy();
		entity.updatedAt = register.updatedAt();
		entity.deleted = register.deleted();
		entity.version = register.version();
		return entity;
	}

	public static CustomerAddress construct(CustomerAddressJpa saved, CryptoTool cryptoTool){
		String addressLine = cryptoTool.decrypt(saved.addressLineCipher, aad(saved.customerId));
		return new CustomerAddress(saved.id, saved.customerId, new AddressLine(addressLine),
				saved.version, saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

}
