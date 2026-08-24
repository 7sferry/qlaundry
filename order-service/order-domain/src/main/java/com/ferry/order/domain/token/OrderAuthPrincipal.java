package com.ferry.order.domain.token;

import com.ferry.order.domain.session.SessionType;
import com.ferry.order.domain.staff.StaffRole;
import lombok.Builder;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record OrderAuthPrincipal(String userId, String username, String fullName, String tenantName,
                                 String tenantId, SessionType sessionType, StaffRole role){

}
