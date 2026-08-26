package com.ferry.user.core.customer.list;

import com.ferry.user.core.tools.UserValidation;
import com.ferry.user.domain.customer.CustomerListSortBy;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortDirection;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerListRequest(String fullName, String phone, String cursor, PageDirection direction,
                                  CustomerListSortBy sortBy, SortDirection sortDir) implements UserValidation{
}
