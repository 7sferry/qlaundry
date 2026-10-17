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
		entity.setId(id);
		entity.setCustomer(customer);
		entity.setAddressLineCipher(cryptoTool.encrypt(register.addressLine().value(), aad(customer.getId())));
		entity.setCreatedBy(register.createdBy());
		entity.setCreatedAt(register.createdAt());
		entity.setUpdatedBy(register.updatedBy());
		entity.setUpdatedAt(register.updatedAt());
		entity.setDeleted(register.deleted());
		entity.setVersion(register.version());
		return entity;
	}

	public static CustomerAddress construct(CustomerAddressJpa saved, CryptoTool cryptoTool){
		String addressLine = cryptoTool.decrypt(saved.getAddressLineCipher(), aad(saved.getCustomerId()));
		return new CustomerAddress(saved.getId(), saved.getCustomerId(), new AddressLine(addressLine),
				saved.getVersion(), saved.isDeleted(), saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(),
				saved.getUpdatedBy());
	}

}
