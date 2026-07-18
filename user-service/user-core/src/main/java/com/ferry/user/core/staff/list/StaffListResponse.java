package com.ferry.user.core.staff.list;

import com.ferry.user.domain.staff.list.StaffAddressListProjection;
import com.ferry.user.domain.staff.list.StaffEmailListProjection;
import com.ferry.user.domain.staff.list.StaffListProjection;
import com.ferry.user.domain.staff.list.StaffPhoneListProjection;

import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffListResponse(List<StaffListProjection> staffs, Map<String, List<StaffPhoneListProjection>> phonesByStaffId,
                                Map<String, List<StaffEmailListProjection>> emailsByStaffId,
                                Map<String, List<StaffAddressListProjection>> addressesByStaffId){
}
