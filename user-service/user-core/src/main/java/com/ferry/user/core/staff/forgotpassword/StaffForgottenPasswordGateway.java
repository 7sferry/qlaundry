package com.ferry.user.core.staff.forgotpassword;

import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.staff.forgottenpassword.StaffEmailForgottenPasswordProjection;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffForgottenPasswordGateway{
	Optional<StaffEmailForgottenPasswordProjection> findEmailWithUsername(UsernameDomain username);
}
