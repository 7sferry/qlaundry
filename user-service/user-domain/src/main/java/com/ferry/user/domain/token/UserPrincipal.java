package com.ferry.user.domain.token;

import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.staff.StaffRole;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record UserPrincipal(String userId, String username, String fullName, String tenantName,
                            String tenantId, SessionType sessionType, StaffRole role){

}
