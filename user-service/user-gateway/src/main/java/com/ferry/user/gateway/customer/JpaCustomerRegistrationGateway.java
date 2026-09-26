package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.registration.CustomerRegistrationGateway;
import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerPhone;
import com.ferry.user.gateway.customer.entity.CustomerAddressJpa;
import com.ferry.user.gateway.customer.entity.CustomerEmailJpa;
import com.ferry.user.gateway.customer.entity.CustomerJpa;
import com.ferry.user.gateway.customer.entity.CustomerPhoneJpa;
import com.ferry.user.gateway.customer.repository.CustomerAddressJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerEmailJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerPhoneJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpa;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaCustomerRegistrationGateway implements CustomerRegistrationGateway{
	private final CustomerJpaRepository customerJpaRepository;
	private final CustomerEmailJpaRepository customerEmailJpaRepository;
	private final CustomerPhoneJpaRepository customerPhoneJpaRepository;
	private final CustomerAddressJpaRepository customerAddressJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;
	private final IdGenerator idGenerator;
	private final CryptoTool cryptoTool;

	@Override
	public Customer save(Customer register){
		String id = idGenerator.generateId();
		TenantJpa tenant = register.tenantId() == null
				? null : tenantJpaRepository.getReferenceById(register.tenantId());
		CustomerJpa saved = customerJpaRepository.save(CustomerJpa.construct(id, register, tenant));
		return CustomerJpa.construct(saved);
	}

	@Override
	public CustomerEmail save(CustomerEmail register){
		String id = idGenerator.generateId();
		CustomerJpa customer = customerJpaRepository.getReferenceById(register.customerId());
		CustomerEmailJpa saved = customerEmailJpaRepository.save(
				CustomerEmailJpa.construct(id, register, customer, cryptoTool));
		return CustomerEmailJpa.construct(saved, cryptoTool);
	}

	@Override
	public CustomerPhone save(CustomerPhone register){
		String id = idGenerator.generateId();
		CustomerJpa customer = customerJpaRepository.getReferenceById(register.customerId());
		CustomerPhoneJpa saved = customerPhoneJpaRepository.save(
				CustomerPhoneJpa.construct(id, register, customer, cryptoTool));
		return CustomerPhoneJpa.construct(saved, cryptoTool);
	}

	@Override
	public CustomerAddress save(CustomerAddress register){
		String id = idGenerator.generateId();
		CustomerJpa customer = customerJpaRepository.getReferenceById(register.customerId());
		CustomerAddressJpa saved = customerAddressJpaRepository.save(
				CustomerAddressJpa.construct(id, register, customer, cryptoTool));
		return CustomerAddressJpa.construct(saved, cryptoTool);
	}

}
