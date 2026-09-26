package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.verification.CustomerVerificationGateway;
import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.gateway.customer.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaCustomerVerificationGateway implements CustomerVerificationGateway{
	private final CustomerJpaRepository customerJpaRepository;

	@Override
	public boolean existsByIdAndTenantId(CustomerId customerId, TenantId tenantId){
		return customerJpaRepository.existsByIdAndTenantIdAndDeletedIsFalse(customerId.value(), tenantId.value());
	}

}
