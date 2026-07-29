package com.ferry.user.core.staff.registration;

import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.staff.StaffAddressDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
import com.ferry.user.domain.staff.StaffPhoneDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffRegistrationGateway{
	StaffDomain save(StaffDomain register);
	StaffEmailDomain save(StaffEmailDomain register);
	StaffAddressDomain save(StaffAddressDomain register);
	StaffPhoneDomain save(StaffPhoneDomain register);
	boolean existsByUsername(UsernameDomain username);
}
