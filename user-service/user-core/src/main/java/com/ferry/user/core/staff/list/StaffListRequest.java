package com.ferry.user.core.staff.list;

import com.ferry.user.core.tools.UserValidation;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortBy;
import com.ferry.utils.pagination.SortDirection;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffListRequest(String fullName, String cursor, PageDirection direction, SortBy sortBy,
                               SortDirection sortDir) implements UserValidation{
}
