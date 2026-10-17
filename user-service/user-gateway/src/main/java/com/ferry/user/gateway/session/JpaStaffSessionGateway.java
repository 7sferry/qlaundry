package com.ferry.user.gateway.session;

import com.ferry.user.core.tools.StaffSessionGateway;
import com.ferry.user.gateway.session.repository.UserSessionJpaRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaStaffSessionGateway implements StaffSessionGateway{
	private final UserSessionJpaRepository userSessionJpaRepository;

	@Override
	public List<String> expireActiveByUserId(String userId, Instant now){
		List<String> activeIds = userSessionJpaRepository.findActiveIdsByUserId(userId, now);
		userSessionJpaRepository.expireActiveByUserId(userId, now);
		return activeIds;
	}
}
