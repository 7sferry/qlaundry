package com.ferry.user.gateway.staff.entity;

import com.ferry.user.domain.common.AddressLineDomain;
import com.ferry.user.domain.staff.StaffAddressDomain;
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
@EqualsAndHashCode(of = "addressLine")
@Entity
@Table(name = "staff_addresses")
public class StaffAddressJpaEntity{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private StaffJpaEntity staff;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "staff_id", insertable = false, updatable = false)
	private String staffId;
	@Column(nullable = false)
	private String addressLine;
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

	public static StaffAddressJpaEntity construct(String id, StaffAddressDomain register, StaffJpaEntity staff){
		StaffAddressJpaEntity entity = new StaffAddressJpaEntity();
		entity.id = id;
		entity.staffId = staff.getId();
		entity.staff = staff;
		entity.addressLine = register.addressLine().value();
		entity.createdBy = register.createdBy();
		entity.updatedAt = register.updatedAt();
		entity.createdAt = register.createdAt();
		entity.updatedBy = register.updatedBy();
		entity.deleted = register.deleted();
		entity.version = register.version();
		return entity;
	}

	public static StaffAddressDomain constructUserAddressDomain(StaffAddressJpaEntity saved){
		return new StaffAddressDomain(saved.id, saved.staffId, new AddressLineDomain(saved.addressLine), saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}
}
