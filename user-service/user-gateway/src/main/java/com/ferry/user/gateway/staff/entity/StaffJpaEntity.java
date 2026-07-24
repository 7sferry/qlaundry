package com.ferry.user.gateway.staff.entity;

import com.ferry.user.domain.DescriptionDomain;
import com.ferry.user.domain.FullNameDomain;
import com.ferry.user.domain.HashedPasswordDomain;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
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
	@Column(name = "tenant_id", insertable = false, updatable = false)
	private String tenantId;
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

	public static StaffDomain construct(StaffJpaEntity saved){
		return new StaffDomain(saved.id, new UsernameDomain(saved.username),
				new HashedPasswordDomain(saved.password), new FullNameDomain(saved.fullName),
				new DescriptionDomain(saved.description), saved.tenantId, saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt,
				saved.updatedBy);
	}

	public static StaffJpaEntity construct(String id, StaffDomain register, TenantJpaEntity tenant){
		StaffJpaEntity entity = new StaffJpaEntity();
		entity.id = id;
		entity.username = register.usernameValue();
		entity.description = register.descriptionValue();
		entity.password = register.passwordValue();
		entity.fullName = register.fullNameValue();
		entity.tenantId = tenant.getId();
		entity.tenant = tenant;
		entity.createdBy = register.createdBy();
		entity.updatedAt = register.updatedAt();
		entity.createdAt = register.createdAt();
		entity.updatedBy = register.updatedBy();
		entity.version = register.version();
		entity.deleted = register.deleted();
		return entity;
	}

}
