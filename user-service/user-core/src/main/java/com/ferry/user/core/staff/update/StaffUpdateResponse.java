package com.ferry.user.core.staff.update;

import com.ferry.user.domain.staff.StaffAddressDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffEmailDomain;
import com.ferry.user.domain.staff.StaffPhoneDomain;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record StaffUpdateResponse(StaffDomain staff, List<StaffEmailDomain> emails, List<StaffPhoneDomain> phones,
                                  List<StaffAddressDomain> addresses){
}
