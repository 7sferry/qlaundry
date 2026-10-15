package com.ferry.user.gateway.analytics;

import com.ferry.user.core.analytics.backfill.UserAnalyticsBackfillGateway;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.gateway.customer.entity.CustomerJpa;
import com.ferry.user.gateway.customer.repository.CustomerJpaRepository;
import com.ferry.user.gateway.staff.entity.StaffJpa;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaUserAnalyticsBackfillGateway implements UserAnalyticsBackfillGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final CustomerJpaRepository customerJpaRepository;

	@Override
	public List<Staff> findStaffsAfter(String tenantId, String afterId, int limit){
		return staffJpaRepository.findBackfillPage(tenantId, afterId, PageRequest.ofSize(limit)).stream()
				.map(StaffJpa::construct)
				.toList();
	}

	@Override
	public List<Customer> findCustomersAfter(String tenantId, String afterId, int limit){
		return customerJpaRepository.findBackfillPage(tenantId, afterId, PageRequest.ofSize(limit)).stream()
				.map(CustomerJpa::construct)
				.toList();
	}

}
