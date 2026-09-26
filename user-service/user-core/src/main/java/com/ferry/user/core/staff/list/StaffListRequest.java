package com.ferry.user.core.staff.list;

import com.ferry.user.core.tools.UserValidation;
import com.ferry.user.domain.staff.StaffListSortBy;
import com.ferry.utils.pagination.SortDirection;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffListRequest(
	String fullName,
	String after,
	String before,
	StaffListSortBy sortBy,
	SortDirection sortDir,
	Integer pageSize) implements UserValidation{
}
