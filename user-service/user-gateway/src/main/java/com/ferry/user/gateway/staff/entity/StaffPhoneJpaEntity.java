package com.ferry.user.gateway.staff.entity;

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
@EqualsAndHashCode(of = "phone")
@Entity
@Table(name = "staff_phones")
public class StaffPhoneJpaEntity{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@JoinColumn(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private StaffJpaEntity staff;
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

}
