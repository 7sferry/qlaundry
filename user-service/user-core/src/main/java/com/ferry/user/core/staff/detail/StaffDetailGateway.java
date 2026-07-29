package com.ferry.user.core.staff.detail;

import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.staff.StaffAddressFilter;
import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.StaffPhoneFilter;
import com.ferry.user.domain.staff.detail.StaffAddressDetailProjection;
import com.ferry.user.domain.staff.detail.StaffDetailProjection;
import com.ferry.user.domain.staff.detail.StaffEmailDetailProjection;
import com.ferry.user.domain.staff.detail.StaffPhoneDetailProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffDetailGateway{
	Optional<StaffDetailProjection> findDetail(UsernameDomain username, TenantIdDomain tenantId);
	List<StaffPhoneDetailProjection> findByFilter(StaffPhoneFilter filter);
	List<StaffAddressDetailProjection> findByFilter(StaffAddressFilter filter);
	List<StaffEmailDetailProjection> findByFilter(StaffEmailFilter filter);
}
