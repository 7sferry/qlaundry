package com.ferry.order.gateway.customer;

import com.ferry.order.core.customer.totals.CustomerOrderTotalsGateway;
import com.ferry.order.domain.customer.totals.CustomerOrderTotalsProjection;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class CustomerOrderTotalsJpaGateway implements CustomerOrderTotalsGateway{
	private final OrderJpaRepository orderJpaRepository;

	@Override
	public List<CustomerOrderTotalsProjection> findTotals(Set<String> customerIds, TenantIdDomain tenantId){
		return orderJpaRepository.findTotalsByCustomerIds(tenantId.value(), customerIds);
	}

}
