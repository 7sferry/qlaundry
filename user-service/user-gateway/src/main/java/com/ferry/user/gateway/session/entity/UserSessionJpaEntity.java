package com.ferry.user.gateway.session.entity;

import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSessionDomain;
import com.ferry.user.gateway.staff.entity.StaffJpaEntity;
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
@EqualsAndHashCode(of = "id")
@Table(name = "user_sessions")
@Entity
public class UserSessionJpaEntity{
	@Id
	private String id;
	@Column(nullable = false)
	Instant expirationTime;
	@Column(nullable = false)
	String userId;
	@JoinColumn(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	UserSessionTypeJpaEntity sessionType;
	@Column(name = "session_type_id", insertable = false, updatable = false)
	@Setter(AccessLevel.PRIVATE)
	private int sessionTypeId;
	@Version
	private Integer version;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false)
	private Instant updatedAt;

	public void setStaff(UserSessionTypeJpaEntity typeJpa){
		this.sessionType = typeJpa;
		this.sessionTypeId = typeJpa.getId();
	}

	public static UserSessionJpaEntity construct(UserSessionDomain userSession, UserSessionTypeJpaEntity sessionType){
		UserSessionJpaEntity entity = new UserSessionJpaEntity();
		entity.id = userSession.id();
		entity.createdAt = userSession.createdAt();
		entity.expirationTime = userSession.expirationTime();
		entity.userId = userSession.userId();
		entity.sessionType = sessionType;
		entity.sessionTypeId = sessionType.getId();
		entity.updatedAt = userSession.updatedAt();
		entity.version = userSession.version();
		return entity;
	}

	public static UserSessionDomain construct(UserSessionJpaEntity saved){
		return new UserSessionDomain(saved.id, saved.expirationTime, saved.userId,
				SessionType.fromValue(saved.sessionTypeId).orElseThrow(), saved.version, saved.createdAt,
				saved.updatedAt);
	}

}
