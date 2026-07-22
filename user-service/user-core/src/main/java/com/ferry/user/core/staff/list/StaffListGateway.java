package com.ferry.user.core.staff.list;

import com.ferry.user.domain.staff.*;
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
	List<StaffListProjection> findByFilter(StaffFilter filter);
	List<StaffPhoneListProjection> findPhonesByFilter(StaffPhoneFilter filter);
	List<StaffEmailListProjection> findEmailsByFilter(StaffEmailFilter filter);
	List<StaffAddressListProjection> findAddressesByFilter(StaffAddressFilter filter);
}
