package com.ferry.user.gateway.session.entity;

import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSession;
import com.ferry.user.gateway.staff.entity.StaffJpa;
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
	@Column(nullable = false)
	private short sessionTypeId;
	@Version
	private Integer version;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false)
	private Instant updatedAt;

	public static UserSessionJpa construct(UserSession userSession){
		UserSessionJpa entity = new UserSessionJpa();
		entity.id = userSession.id();
		entity.createdAt = userSession.createdAt();
		entity.expirationTime = userSession.expirationTime();
		entity.userId = userSession.userId();
		entity.sessionTypeId = userSession.sessionTypeValue();
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
