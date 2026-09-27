package com.ferry.promotion.domain.token;

import com.ferry.promotion.domain.session.SessionType;
import com.ferry.promotion.domain.staff.StaffRole;
import lombok.Builder;

import java.time.ZoneId;
import java.time.ZoneOffset;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record PromotionAuthPrincipal(
	String userId,
	String username,
	String fullName,
	String tenantName,
	String tenantId,
	ZoneId timeZone,
	SessionType sessionType,
	StaffRole role){

	public PromotionAuthPrincipal{
		timeZone = timeZone == null ? ZoneOffset.UTC : timeZone;
	}

}
