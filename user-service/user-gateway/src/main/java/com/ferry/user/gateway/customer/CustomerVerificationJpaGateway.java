package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.verification.CustomerVerificationGateway;
import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.gateway.customer.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class CustomerVerificationJpaGateway implements CustomerVerificationGateway{
	private final CustomerJpaRepository customerJpaRepository;

	@Override
	public boolean existsByIdAndTenantId(CustomerIdDomain customerId, TenantIdDomain tenantId){
		return customerJpaRepository.existsByIdAndTenantIdAndDeletedIsFalse(customerId.value(), tenantId.value());
	}

}
