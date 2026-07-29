package com.ferry.user.gateway.staff.entity;

import com.ferry.user.domain.common.PhoneDomain;
import com.ferry.user.domain.staff.StaffPhoneDomain;
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
@EqualsAndHashCode(of = "phone")
@Entity
@Table(name = "staff_phones")
public class StaffPhoneJpaEntity{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@JoinColumn(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private StaffJpaEntity staff;
	@Column(name = "staff_id", insertable = false, updatable = false)
	@Setter(AccessLevel.PRIVATE)
	private String staffId;
	@Column(nullable = false, length = 20)
	private String phone;
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

	public static StaffPhoneJpaEntity construct(String id, StaffPhoneDomain register, StaffJpaEntity staff){
		StaffPhoneJpaEntity entity = new StaffPhoneJpaEntity();
		entity.id = id;
		entity.staffId = staff.getId();
		entity.staff = staff;
		entity.phone = register.phone().value();
		entity.createdBy = register.createdBy();
		entity.updatedAt = register.updatedAt();
		entity.createdAt = register.createdAt();
		entity.updatedBy = register.updatedBy();
		entity.deleted = register.deleted();
		entity.version = register.version();
		return entity;
	}

	public static StaffPhoneDomain construct(StaffPhoneJpaEntity saved){
		return new StaffPhoneDomain(saved.id, saved.staffId, new PhoneDomain(saved.phone), saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}
}
