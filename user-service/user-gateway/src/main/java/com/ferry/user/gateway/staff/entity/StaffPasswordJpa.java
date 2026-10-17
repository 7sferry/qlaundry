package com.ferry.user.gateway.staff.entity;

import com.ferry.user.domain.common.HashedPassword;
import com.ferry.user.domain.staff.StaffPassword;
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
public class StaffPasswordJpa{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@Getter(AccessLevel.PRIVATE)
	private StaffJpa staff;
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

	public void setStaff(StaffJpa staff){
		this.staff = staff;
		this.staffId = staff.getId();
	}

	public static StaffPasswordJpa construct(String id, StaffPassword register, StaffJpa staff){
		StaffPasswordJpa entity = new StaffPasswordJpa();
		entity.setId(id);
		entity.setStaff(staff);
		entity.setPassword(register.passwordValue());
		entity.setCreatedBy(register.createdBy());
		entity.setUpdatedAt(register.updatedAt());
		entity.setCreatedAt(register.createdAt());
		entity.setUpdatedBy(register.updatedBy());
		entity.setDeleted(register.deleted());
		entity.setVersion(register.version());
		return entity;
	}

	public static StaffPassword construct(StaffPasswordJpa saved){
		return new StaffPassword(saved.getId(), saved.getStaffId(), new HashedPassword(saved.getPassword()), saved.getVersion(),
				saved.isDeleted(), saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(), saved.getUpdatedBy());
	}
}
