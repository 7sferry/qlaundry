package com.ferry.user.core.staff.logout;

import com.ferry.user.domain.session.UserSessionDomain;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffLogoutGateway{
	Optional<UserSessionDomain> findSessionById(String id);
	UserSessionDomain save(UserSessionDomain userSession);
}
