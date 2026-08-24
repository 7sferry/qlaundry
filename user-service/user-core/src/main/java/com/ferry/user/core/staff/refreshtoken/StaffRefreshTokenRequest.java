package com.ferry.user.core.staff.refreshtoken;

import com.ferry.user.core.tools.UserValidation;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffRefreshTokenRequest(String refreshToken) implements UserValidation{
}
