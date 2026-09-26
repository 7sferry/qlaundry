package com.ferry.user.core.staff.registration;

import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.staff.StaffAddress;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffEmail;
import com.ferry.user.domain.staff.StaffPassword;
import com.ferry.user.domain.staff.StaffPhone;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffRegistrationGateway{
	Staff save(Staff register);
	StaffPassword save(StaffPassword register);
	StaffEmail save(StaffEmail register);
	StaffAddress save(StaffAddress register);
	StaffPhone save(StaffPhone register);
	boolean existsByUsername(Username username);
}
