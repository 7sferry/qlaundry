package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.registration.CustomerRegistrationGateway;
import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
import com.ferry.user.gateway.customer.entity.CustomerAddressJpaEntity;
import com.ferry.user.gateway.customer.entity.CustomerEmailJpaEntity;
import com.ferry.user.gateway.customer.entity.CustomerJpaEntity;
import com.ferry.user.gateway.customer.entity.CustomerPhoneJpaEntity;
import com.ferry.user.gateway.customer.repository.CustomerAddressJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerEmailJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerPhoneJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class CustomerRegistrationJpaGateway implements CustomerRegistrationGateway{
	private final CustomerJpaRepository customerJpaRepository;
	private final CustomerEmailJpaRepository customerEmailJpaRepository;
	private final CustomerPhoneJpaRepository customerPhoneJpaRepository;
	private final CustomerAddressJpaRepository customerAddressJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;
	private final IdGenerator idGenerator;
	private final CryptoTool cryptoTool;

	@Override
	public CustomerDomain save(CustomerDomain register){
		String id = idGenerator.generateId();
		TenantJpaEntity tenant = register.tenantId() == null
				? null : tenantJpaRepository.getReferenceById(register.tenantId());
		CustomerJpaEntity saved = customerJpaRepository.save(CustomerJpaEntity.construct(id, register, tenant));
		return CustomerJpaEntity.construct(saved);
	}

	@Override
	public CustomerEmailDomain save(CustomerEmailDomain register){
		String id = idGenerator.generateId();
		CustomerJpaEntity customer = customerJpaRepository.getReferenceById(register.customerId());
		CustomerEmailJpaEntity saved = customerEmailJpaRepository.save(
				CustomerEmailJpaEntity.construct(id, register, customer, cryptoTool));
		return CustomerEmailJpaEntity.construct(saved, cryptoTool);
	}

	@Override
	public CustomerPhoneDomain save(CustomerPhoneDomain register){
		String id = idGenerator.generateId();
		CustomerJpaEntity customer = customerJpaRepository.getReferenceById(register.customerId());
		CustomerPhoneJpaEntity saved = customerPhoneJpaRepository.save(
				CustomerPhoneJpaEntity.construct(id, register, customer, cryptoTool));
		return CustomerPhoneJpaEntity.construct(saved, cryptoTool);
	}

	@Override
	public CustomerAddressDomain save(CustomerAddressDomain register){
		String id = idGenerator.generateId();
		CustomerJpaEntity customer = customerJpaRepository.getReferenceById(register.customerId());
		CustomerAddressJpaEntity saved = customerAddressJpaRepository.save(
				CustomerAddressJpaEntity.construct(id, register, customer, cryptoTool));
		return CustomerAddressJpaEntity.construct(saved, cryptoTool);
	}

}
