package com.ferry.user.core.staff.resetpassword;

import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffPasswordDomain;
import com.ferry.user.domain.staff.StaffPasswordProjection;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffResetPasswordGateway{
	Optional<StaffDomain> findByUsername(UsernameDomain username);

	Optional<StaffPasswordProjection> findCurrentPassword(String staffId);

	List<StaffPasswordProjection> findRecentPasswords(String staffId, Instant since);

	void save(StaffPasswordDomain password);
}
