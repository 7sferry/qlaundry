package com.ferry.user.gateway.staff.entity;

import com.ferry.user.domain.common.HashedPasswordDomain;
import com.ferry.user.domain.staff.StaffPasswordDomain;
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
@Table(name = "staff_passwords")
public class StaffPasswordJpaEntity{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private StaffJpaEntity staff;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "staff_id", insertable = false, updatable = false)
	private String staffId;
	@Column(nullable = false)
	private String password;
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

	public void setStaff(StaffJpaEntity staff){
		this.staff = staff;
		this.staffId = staff.getId();
	}

	public static StaffPasswordJpaEntity construct(String id, StaffPasswordDomain register, StaffJpaEntity staff){
		StaffPasswordJpaEntity entity = new StaffPasswordJpaEntity();
		entity.id = id;
		entity.staffId = staff.getId();
		entity.staff = staff;
		entity.password = register.passwordValue();
		entity.createdBy = register.createdBy();
		entity.updatedAt = register.updatedAt();
		entity.createdAt = register.createdAt();
		entity.updatedBy = register.updatedBy();
		entity.deleted = register.deleted();
		entity.version = register.version();
		return entity;
	}

	public static StaffPasswordDomain construct(StaffPasswordJpaEntity saved){
		return new StaffPasswordDomain(saved.id, saved.staffId, new HashedPasswordDomain(saved.password), saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}
}
