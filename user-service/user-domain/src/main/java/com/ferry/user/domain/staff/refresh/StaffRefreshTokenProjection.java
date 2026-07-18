package com.ferry.user.domain.staff.refresh;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffRefreshTokenProjection(String id, Instant expirationTime, String userId, Integer sessionTypeId){
}
