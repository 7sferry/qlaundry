package com.ferry.user.core.staff.detail;

import com.ferry.user.domain.staff.detail.StaffAddressDetailProjection;
import com.ferry.user.domain.staff.detail.StaffDetailProjection;
import com.ferry.user.domain.staff.detail.StaffEmailDetailProjection;
import com.ferry.user.domain.staff.detail.StaffPhoneDetailProjection;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffDetailResponse(StaffDetailProjection staff, List<StaffPhoneDetailProjection> phones,
                                  List<StaffEmailDetailProjection> emails,
                                  List<StaffAddressDetailProjection> addresses){
}
