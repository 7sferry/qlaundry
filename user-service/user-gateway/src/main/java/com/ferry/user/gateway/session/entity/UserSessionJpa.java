package com.ferry.user.gateway.session.entity;

import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSession;
import com.ferry.user.gateway.staff.entity.StaffJpa;
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
public class UserSessionJpa{
	@Id
	private String id;
	@Column(nullable = false)
	Instant expirationTime;
	@Column(nullable = false)
	String userId;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	UserSessionTypeJpa sessionType;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "session_type_id", insertable = false, updatable = false)
	private short sessionTypeId;
	@Version
	private Integer version;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false)
	private Instant updatedAt;

	public void setStaff(UserSessionTypeJpa typeJpa){
		this.sessionType = typeJpa;
		this.sessionTypeId = typeJpa.getId();
	}

	public static UserSessionJpa construct(UserSession userSession, UserSessionTypeJpa sessionType){
		UserSessionJpa entity = new UserSessionJpa();
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

	public static UserSession construct(UserSessionJpa saved){
		return new UserSession(saved.id, saved.expirationTime, saved.userId,
				SessionType.fromValue(saved.sessionTypeId).orElseThrow(), saved.version, saved.createdAt,
				saved.updatedAt);
	}

}
