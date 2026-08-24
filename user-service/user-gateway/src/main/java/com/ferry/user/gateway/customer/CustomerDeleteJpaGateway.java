package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.delete.CustomerDeleteGateway;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.gateway.customer.entity.CustomerJpaEntity;
import com.ferry.user.gateway.customer.repository.CustomerAddressJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerEmailJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerPhoneJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class CustomerDeleteJpaGateway implements CustomerDeleteGateway{
	private final CustomerJpaRepository customerJpaRepository;
	private final CustomerEmailJpaRepository customerEmailJpaRepository;
	private final CustomerPhoneJpaRepository customerPhoneJpaRepository;
	private final CustomerAddressJpaRepository customerAddressJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;

	@Override
	public Optional<CustomerDomain> findById(CustomerIdDomain customerId, TenantIdDomain tenantId){
		return customerJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(customerId.value(), tenantId.value())
				.map(CustomerJpaEntity::construct);
	}

	@Override
	public CustomerDomain save(CustomerDomain customer){
		TenantJpaEntity tenant = customer.tenantId() == null
				? null : tenantJpaRepository.getReferenceById(customer.tenantId());
		CustomerJpaEntity saved = customerJpaRepository.save(
				CustomerJpaEntity.construct(customer.id(), customer, tenant));
		return CustomerJpaEntity.construct(saved);
	}

	@Override
	public void deleteContacts(String customerId, String updatedBy){
		customerEmailJpaRepository.softDeleteByCustomerId(customerId, updatedBy);
		customerPhoneJpaRepository.softDeleteByCustomerId(customerId, updatedBy);
		customerAddressJpaRepository.softDeleteByCustomerId(customerId, updatedBy);
	}

}
