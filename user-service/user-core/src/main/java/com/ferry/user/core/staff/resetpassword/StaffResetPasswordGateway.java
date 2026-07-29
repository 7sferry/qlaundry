package com.ferry.user.core.staff.resetpassword;

import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.staff.StaffDomain;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffResetPasswordGateway{
	Optional<StaffDomain> findByUsername(UsernameDomain username);

	void save(StaffDomain updatedStaff);
}
