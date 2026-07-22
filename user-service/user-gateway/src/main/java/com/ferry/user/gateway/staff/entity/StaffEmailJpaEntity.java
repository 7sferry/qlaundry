package com.ferry.user.gateway.staff.entity;

import com.ferry.user.domain.EmailDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
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
@EqualsAndHashCode(of = "email")
@Entity
@Table(name = "staff_emails")
public class StaffEmailJpaEntity{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@JoinColumn(nullable = false, insertable = false, updatable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private StaffJpaEntity staff;
	@Column(name = "staff_id")
	private String staffId;
	@Column(nullable = false, length = 100)
	private String email;
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

	public static StaffEmailJpaEntity create(String id, StaffEmailDomain register, StaffJpaEntity staff){
		StaffEmailJpaEntity entity = new StaffEmailJpaEntity();
		entity.id = id;
		entity.staffId = register.staffId();
		entity.staff = staff;
		entity.email = register.email().value();
		entity.createdBy = register.createdBy();
		entity.updatedAt = register.updatedAt();
		entity.createdAt = register.createdAt();
		entity.updatedBy = register.updatedBy();
		entity.version = register.version();
		return entity;
	}

	public static StaffEmailDomain constructUserEmailDomain(StaffEmailJpaEntity saved){
		return new StaffEmailDomain(saved.id, saved.staffId, new EmailDomain(saved.email), saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}
}
