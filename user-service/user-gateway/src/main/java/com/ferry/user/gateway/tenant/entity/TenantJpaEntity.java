package com.ferry.user.gateway.tenant.entity;

import com.ferry.user.domain.tenant.TenantDomain;
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
@Table(name = "tenants")
public class TenantJpaEntity{
	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(nullable = false, length = 100)
	private String fullName;
	@Column
	private String description;
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

	public static TenantJpaEntity create(String id, TenantDomain tenant){
		TenantJpaEntity entity = new TenantJpaEntity();
		entity.id = id;
		entity.createdAt = tenant.createdAt();
		entity.updatedAt = tenant.updatedAt();
		entity.createdBy = entity.id;
		entity.updatedBy = entity.id;
		entity.description = tenant.descriptionValue();
		entity.fullName = tenant.fullNameValue();
		entity.version = tenant.version();
		entity.deleted = tenant.deleted();
		return entity;
	}

}
