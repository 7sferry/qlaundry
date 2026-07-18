package com.ferry.user.core.staff.list;

import com.ferry.user.domain.staff.StaffListFilter;
import com.ferry.user.domain.staff.list.StaffAddressListProjection;
import com.ferry.user.domain.staff.list.StaffEmailListProjection;
import com.ferry.user.domain.staff.list.StaffListProjection;
import com.ferry.user.domain.staff.list.StaffPhoneListProjection;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffListGateway{
	List<StaffListProjection> findByFilter(StaffListFilter filter);
	List<StaffPhoneListProjection> findPhonesByStaffIds(List<String> staffIds);
	List<StaffEmailListProjection> findEmailsByStaffIds(List<String> staffIds);
	List<StaffAddressListProjection> findAddressesByStaffIds(List<String> staffIds);
}
