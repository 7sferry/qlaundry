package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.delete.CustomerDeleteGateway;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.gateway.customer.entity.CustomerJpa;
import com.ferry.user.gateway.customer.repository.CustomerAddressJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerEmailJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerPhoneJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpa;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaCustomerDeleteGateway implements CustomerDeleteGateway{
	private final CustomerJpaRepository customerJpaRepository;
	private final CustomerEmailJpaRepository customerEmailJpaRepository;
	private final CustomerPhoneJpaRepository customerPhoneJpaRepository;
	private final CustomerAddressJpaRepository customerAddressJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;

	@Override
	public Optional<Customer> findById(CustomerId customerId, TenantId tenantId){
		return customerJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(customerId.value(), tenantId.value())
				.map(CustomerJpa::construct);
	}

	@Override
	public Customer save(Customer customer){
		TenantJpa tenant = customer.tenantId() == null
				? null : tenantJpaRepository.getReferenceById(customer.tenantId());
		CustomerJpa saved = customerJpaRepository.save(
				CustomerJpa.construct(customer.id(), customer, tenant));
		return CustomerJpa.construct(saved);
	}

	@Override
	public void deleteContacts(String customerId, String updatedBy){
		customerEmailJpaRepository.softDeleteByCustomerId(customerId, updatedBy);
		customerPhoneJpaRepository.softDeleteByCustomerId(customerId, updatedBy);
		customerAddressJpaRepository.softDeleteByCustomerId(customerId, updatedBy);
	}

}
