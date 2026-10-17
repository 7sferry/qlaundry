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
		return new Staff(saved.getId(), new Username(saved.getUsername()),
				new FullName(saved.getFullName()),
				new Description(saved.getDescription()), saved.getTenantId(),
				StaffRole.findByValue(saved.getRoleId()).orElseThrow(), saved.getVersion(),
				saved.isDeleted(), saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(),
				saved.getUpdatedBy());
	}

	public static StaffJpa construct(String id, Staff register, TenantJpa tenant, StaffRole role){
		StaffJpa entity = new StaffJpa();
		entity.setId(id);
		entity.setUsername(register.usernameValue());
		entity.setDescription(register.descriptionValue());
		entity.setFullName(register.fullNameValue());
		entity.setTenant(tenant);
		entity.setRoleId(role.getValue());
		entity.setCreatedBy(register.createdBy());
		entity.setUpdatedAt(register.updatedAt());
		entity.setCreatedAt(register.createdAt());
		entity.setUpdatedBy(register.updatedBy());
		entity.setVersion(register.version());
		entity.setDeleted(register.deleted());
		return entity;
	}

}
