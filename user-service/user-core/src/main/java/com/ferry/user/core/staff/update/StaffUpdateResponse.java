package com.ferry.user.core.staff.update;

import com.ferry.user.domain.staff.StaffAddress;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffEmail;
import com.ferry.user.domain.staff.StaffPhone;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffUpdateResponse(
	Staff staff,
	List<StaffEmail> emails,
	List<StaffPhone> phones,
	List<StaffAddress> addresses){
}
