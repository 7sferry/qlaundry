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
	@JoinColumn(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private StaffJpaEntity staff;
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

	public static StaffEmailDomain constructUserEmailDomain(StaffEmailJpaEntity saved){
		return new StaffEmailDomain(saved.id, saved.staff.getId(), new EmailDomain(saved.email), saved.version,
				saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}
}
