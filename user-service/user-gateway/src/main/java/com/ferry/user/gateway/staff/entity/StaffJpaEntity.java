package com.ferry.user.gateway.staff.entity;

import com.ferry.user.domain.DescriptionDomain;
import com.ferry.user.domain.FullNameDomain;
import com.ferry.user.domain.HashedPasswordDomain;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffRole;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
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
@EqualsAndHashCode(of = "username")
@Entity
@Table(name = "staffs")
public class StaffJpaEntity{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(unique = true, nullable = false, length = 50)
	private String username;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false, length = 100)
	private String fullName;
	@Column
	private String description;
	@JoinColumn(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private TenantJpaEntity tenant;
	@Setter(AccessLevel.PRIVATE)
	@Column(name = "tenant_id", insertable = false, updatable = false)
	private String tenantId;
	@JoinColumn
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private StaffRoleJpaEntity role;
	@Setter(AccessLevel.PRIVATE)
	@Column(name = "role_id", insertable = false, updatable = false, nullable = false, columnDefinition = "SMALLINT DEFAULT 1")
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

	public void setTenant(TenantJpaEntity tenant){
		this.tenant = tenant;
		this.tenantId = tenant.getId();
	}

	public void setRole(StaffRoleJpaEntity role){
		this.role = role;
		this.roleId = role.getId();
	}

	public static StaffDomain construct(StaffJpaEntity saved){
		return new StaffDomain(saved.id, new UsernameDomain(saved.username),
				new HashedPasswordDomain(saved.password), new FullNameDomain(saved.fullName),
				new DescriptionDomain(saved.description), saved.tenantId,
				StaffRole.findByValue(saved.roleId).orElseThrow(), saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt,
				saved.updatedBy);
	}

	public static StaffJpaEntity construct(String id, StaffDomain register, TenantJpaEntity tenant, StaffRoleJpaEntity role){
		StaffJpaEntity entity = new StaffJpaEntity();
		entity.id = id;
		entity.username = register.usernameValue();
		entity.description = register.descriptionValue();
		entity.password = register.passwordValue();
		entity.fullName = register.fullNameValue();
		entity.tenantId = tenant.getId();
		entity.tenant = tenant;
		entity.role = role;
		entity.roleId = role.getId();
		entity.createdBy = register.createdBy();
		entity.updatedAt = register.updatedAt();
		entity.createdAt = register.createdAt();
		entity.updatedBy = register.updatedBy();
		entity.version = register.version();
		entity.deleted = register.deleted();
		return entity;
	}

}
