package com.ferry.user.gateway.customer.entity;

import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.gateway.tenant.entity.TenantJpa;
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
@Table(name = "customers", indexes = @Index(name = "idx_customers_tenant_id", columnList = "tenant_id"))
public class CustomerJpa{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY)
	@Getter(AccessLevel.PRIVATE)
	private TenantJpa tenant;
	@Setter(AccessLevel.PRIVATE)
	@Column(name = "tenant_id", insertable = false, updatable = false)
	private String tenantId;
	@Column(nullable = false, length = 100)
	private String fullName;
	@Column
	private String notes;
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

	public void setTenant(TenantJpa tenant){
		this.tenant = tenant;
		this.tenantId = tenant == null ? null : tenant.getId();
	}

	public static CustomerJpa construct(String id, Customer customer, TenantJpa tenant){
		CustomerJpa entity = new CustomerJpa();
		entity.id = id;
		entity.tenant = tenant;
		entity.tenantId = tenant == null ? null : tenant.getId();
		entity.fullName = customer.fullNameValue();
		entity.notes = customer.notesValue();
		entity.createdBy = customer.createdBy();
		entity.createdAt = customer.createdAt();
		entity.updatedBy = customer.updatedBy();
		entity.updatedAt = customer.updatedAt();
		entity.deleted = customer.deleted();
		entity.version = customer.version();
		return entity;
	}

	public static Customer construct(CustomerJpa saved){
		return new Customer(saved.id, saved.tenantId, new FullName(saved.fullName),
				new Description(saved.notes), saved.version, saved.deleted, saved.createdAt, saved.createdBy,
				saved.updatedAt, saved.updatedBy);
	}

}
