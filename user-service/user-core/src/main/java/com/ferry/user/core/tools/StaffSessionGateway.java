package com.ferry.user.core.tools;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface StaffSessionGateway{
	List<String> expireActiveByUserId(String userId, Instant now);
}
