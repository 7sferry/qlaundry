package com.ferry.user.domain.staff.list;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffListProjection(String id, String description, String fullName, Instant createdAt, String username){
}
