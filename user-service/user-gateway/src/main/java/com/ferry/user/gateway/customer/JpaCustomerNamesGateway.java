package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.names.CustomerNamesGateway;
import com.ferry.user.domain.customer.names.CustomerNameProjection;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.gateway.customer.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaCustomerNamesGateway implements CustomerNamesGateway{
	private final CustomerJpaRepository customerJpaRepository;

	@Override
	public List<CustomerNameProjection> findNames(TenantId tenantId, Set<String> customerIds){
		return customerJpaRepository.findNamesByIds(tenantId.value(), customerIds);
	}

}
