package com.ferry.user.core.customer.list;

import com.ferry.user.core.tools.UserValidation;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerListRequest(String fullName, String phone) implements UserValidation{
}
