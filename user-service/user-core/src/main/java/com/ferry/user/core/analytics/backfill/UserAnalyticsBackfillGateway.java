package com.ferry.user.core.analytics.backfill;

import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.staff.Staff;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface UserAnalyticsBackfillGateway{
	List<Staff> findStaffsAfter(String tenantId, String afterId, int limit);

	List<Customer> findCustomersAfter(String tenantId, String afterId, int limit);
}
