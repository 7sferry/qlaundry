package com.ferry.user.gateway.staff.entity;

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
	@Column(nullable = false)
	private String description;
	@JoinColumn(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private TenantJpaEntity tenant;
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

}
