package com.ferry.user.gateway.staff.entity;

import com.ferry.user.domain.common.Description;
import com.ferry.user.domain.common.FullName;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.gateway.tenant.entity.TenantJpa;
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
@Table(name = "staffs")
public class StaffJpa{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(unique = true, length = 50)
	private String username;
	@Column(nullable = false, length = 100)
	private String fullName;
	@Column
	private String description;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Getter(AccessLevel.PRIVATE)
	private TenantJpa tenant;
	@Column(nullable = false, name = "tenant_id", insertable = false, updatable = false)
	@Setter(AccessLevel.PRIVATE)
	private String tenantId;
	@Column(nullable = false, columnDefinition = "SMALLINT DEFAULT 1")
	private short roleId;
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
		this.tenantId = tenant.getId();
	}

	public static Staff construct(StaffJpa saved){
		return new Staff(saved.id, new Username(saved.username),
				new FullName(saved.fullName),
				new Description(saved.description), saved.tenantId,
				StaffRole.findByValue(saved.roleId).orElseThrow(), saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt,
				saved.updatedBy);
	}

	public static StaffJpa construct(String id, Staff register, TenantJpa tenant, StaffRole role){
		StaffJpa entity = new StaffJpa();
		entity.id = id;
		entity.username = register.usernameValue();
		entity.description = register.descriptionValue();
		entity.fullName = register.fullNameValue();
		entity.tenantId = tenant.getId();
		entity.tenant = tenant;
		entity.roleId = role.getValue();
		entity.createdBy = register.createdBy();
		entity.updatedAt = register.updatedAt();
		entity.createdAt = register.createdAt();
		entity.updatedBy = register.updatedBy();
		entity.version = register.version();
		entity.deleted = register.deleted();
		return entity;
	}

}
