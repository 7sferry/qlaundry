package com.ferry.user.core.tools;

import com.ferry.user.core.staff.constant.TokenConstant;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultStaffSessionRevoker implements StaffSessionRevoker{
	private final StaffSessionGateway gateway;
	private final UserCacheManager cacheManager;

	@Override
	public void revokeAll(String staffId){
		for(String sessionId : gateway.expireActiveByUserId(staffId, Instant.now())){
			cacheManager.delete(TokenConstant.REFRESH_KEY + sessionId);
			cacheManager.delete(TokenConstant.ACCESS_KEY + sessionId);
			cacheManager.delete(TokenConstant.ROTATED_KEY + sessionId);
		}
	}
}
