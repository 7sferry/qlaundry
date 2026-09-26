package com.ferry.promotion.domain.token;

import com.ferry.promotion.domain.session.SessionType;
import com.ferry.promotion.domain.staff.StaffRole;
import lombok.Builder;

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
	SessionType sessionType,
	StaffRole role){

}
