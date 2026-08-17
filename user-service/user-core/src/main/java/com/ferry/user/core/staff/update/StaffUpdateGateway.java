package com.ferry.user.core.staff.update;

import com.ferry.user.domain.staff.StaffAddressDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
import com.ferry.user.domain.staff.StaffPasswordDomain;
import com.ferry.user.domain.staff.StaffPasswordProjection;
import com.ferry.user.domain.staff.StaffPhoneDomain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffUpdateGateway{
	Optional<StaffDomain> findById(String id);
	StaffDomain save(StaffDomain staff);

	Optional<StaffPasswordProjection> findCurrentPassword(String staffId);
	List<StaffPasswordProjection> findRecentPasswords(String staffId, Instant since);
	void save(StaffPasswordDomain password);

	List<StaffEmailDomain> findEmailsByStaffId(String staffId);
	List<StaffPhoneDomain> findPhonesByStaffId(String staffId);
	List<StaffAddressDomain> findAddressesByStaffId(String staffId);

	void deleteEmails(String staffId, String updatedBy);
	void deletePhones(String staffId, String updatedBy);
	void deleteAddresses(String staffId, String updatedBy);

	StaffEmailDomain save(StaffEmailDomain email);
	StaffPhoneDomain save(StaffPhoneDomain phone);
	StaffAddressDomain save(StaffAddressDomain address);
}
