package com.ferry.user.core.staff.resetpassword;

import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffPassword;
import com.ferry.user.domain.staff.StaffPasswordProjection;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffResetPasswordGateway{
	Optional<Staff> findByUsername(Username username);

	Optional<StaffPasswordProjection> findCurrentPassword(String staffId);

	List<StaffPasswordProjection> findRecentPasswords(String staffId, Instant since);

	void save(StaffPassword password);
}
