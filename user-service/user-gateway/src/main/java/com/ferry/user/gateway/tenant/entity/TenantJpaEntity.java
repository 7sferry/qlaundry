package com.ferry.user.gateway.tenant.entity;

import com.ferry.user.domain.common.DescriptionDomain;
import com.ferry.user.domain.common.FullNameDomain;
import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.tenant.TenantStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private TenantStatusJpaEntity status;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "status_id", insertable = false, updatable = false, columnDefinition = "SMALLINT DEFAULT 1")
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

	public void setStatus(TenantStatusJpaEntity status){
		this.status = status;
		this.statusId = status.getId();
	}

	public static TenantDomain construct(TenantJpaEntity saved){
		return new TenantDomain(saved.id, new FullNameDomain(saved.fullName), new DescriptionDomain(saved.description),
				TenantStatus.findByValue(saved.statusId).orElseThrow(), saved.version, saved.deleted, saved.createdAt,
				saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

	public static TenantJpaEntity construct(String id, TenantDomain tenant, TenantStatusJpaEntity status){
		TenantJpaEntity entity = new TenantJpaEntity();
		entity.id = id;
		entity.createdAt = tenant.createdAt();
		entity.updatedAt = tenant.updatedAt();
		entity.createdBy = entity.id;
		entity.updatedBy = entity.id;
		entity.description = tenant.descriptionValue();
		entity.fullName = tenant.fullNameValue();
		entity.status = status;
		entity.statusId = status.getId();
		entity.version = tenant.version();
		entity.deleted = tenant.deleted();
		return entity;
	}

}
