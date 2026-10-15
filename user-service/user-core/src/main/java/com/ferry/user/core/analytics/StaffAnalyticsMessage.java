package com.ferry.user.core.analytics;

import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffRole;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record StaffAnalyticsMessage(
	String tenantId,
	String staffId,
	StaffRole role,
	boolean deleted,
	int version,
	Long createdAt,
	Long updatedAt){

	public static StaffAnalyticsMessage from(Staff staff){
		return new StaffAnalyticsMessage(staff.tenantId(), staff.id(), staff.role(), staff.deleted(),
				staff.version() == null ? 0 : staff.version(), epochMillis(staff.createdAt()),
				epochMillis(staff.updatedAt()));
	}

	private static Long epochMillis(Instant instant){
		return instant == null ? null : instant.toEpochMilli();
	}

}
