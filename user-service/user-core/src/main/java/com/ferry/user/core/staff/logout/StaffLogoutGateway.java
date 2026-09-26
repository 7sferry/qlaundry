package com.ferry.user.core.staff.logout;

import com.ferry.user.domain.session.UserSession;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffLogoutGateway{
	Optional<UserSession> findSessionById(String id);
	UserSession save(UserSession userSession);
}
