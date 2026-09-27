package com.ferry.user.domain.staff.detail;

import com.ferry.user.domain.staff.StaffRole;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffDetailProjection(
	String id,
	String description,
	String fullName,
	Instant createdAt,
	String username,
	short roleId){
	public StaffRole role(){
		return StaffRole.findByValue(roleId).orElseThrow();
	}
}
