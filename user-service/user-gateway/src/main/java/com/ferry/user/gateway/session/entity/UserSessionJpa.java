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
		entity.setId(userSession.id());
		entity.setCreatedAt(userSession.createdAt());
		entity.setExpirationTime(userSession.expirationTime());
		entity.setUserId(userSession.userId());
		entity.setSessionTypeId(userSession.sessionTypeValue());
		entity.setUpdatedAt(userSession.updatedAt());
		entity.setVersion(userSession.version());
		return entity;
	}

	public static UserSession construct(UserSessionJpa saved){
		return new UserSession(saved.getId(), saved.getExpirationTime(), saved.getUserId(),
				SessionType.fromValue(saved.getSessionTypeId()).orElseThrow(), saved.getVersion(), saved.getCreatedAt(),
				saved.getUpdatedAt());
	}

}
