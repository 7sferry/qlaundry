package com.ferry.user.core.staff.update;

import com.ferry.user.domain.staff.StaffAddress;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffEmail;
import com.ferry.user.domain.staff.StaffPassword;
import com.ferry.user.domain.staff.StaffPasswordProjection;
import com.ferry.user.domain.staff.StaffPhone;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffUpdateGateway{
	Optional<Staff> findById(String id);
	Staff save(Staff staff);

	Optional<StaffPasswordProjection> findCurrentPassword(String staffId);
	List<StaffPasswordProjection> findRecentPasswords(String staffId, Instant since);
	void save(StaffPassword password);

	List<StaffEmail> findEmailsByStaffId(String staffId);
	List<StaffPhone> findPhonesByStaffId(String staffId);
	List<StaffAddress> findAddressesByStaffId(String staffId);

	void deleteEmails(String staffId, String updatedBy);
	void deletePhones(String staffId, String updatedBy);
	void deleteAddresses(String staffId, String updatedBy);

	StaffEmail save(StaffEmail email);
	StaffPhone save(StaffPhone phone);
	StaffAddress save(StaffAddress address);
}
