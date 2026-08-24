package com.ferry.user.core.customer.list;

import com.ferry.user.core.tools.UserValidation;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortBy;
import com.ferry.utils.pagination.SortDirection;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerListRequest(String fullName, String phone, String cursor, PageDirection direction,
                                  SortBy sortBy, SortDirection sortDir) implements UserValidation{
}
