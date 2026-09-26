package com.ferry.user.domain.session;

import com.ferry.user.domain.common.exception.InvalidUserStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Builder(toBuilder = true)
public record UserSessionDomain(
	String id,
	Instant expirationTime,
	String userId,
	SessionType sessionType,
	Integer version,
	Instant createdAt,
	Instant updatedAt){
	public UserSessionDomain{
		if(expirationTime == null){
			throw new InvalidUserStateException("expirationTime cannot be null");
		}
		if(userId == null){
			throw new InvalidUserStateException("userId cannot be null");
		}
		if(sessionType == null){
			throw new InvalidUserStateException("session type cannot be null");
		}
	}

	public short sessionTypeValue(){
		return sessionType.getValue();
	}

	public static UserSessionDomain create(String id, Instant expirationTime, String userId, SessionType sessionType){
		Instant now = Instant.now();
		return new UserSessionDomain(id, expirationTime, userId, sessionType, null, now, now);
	}
}
