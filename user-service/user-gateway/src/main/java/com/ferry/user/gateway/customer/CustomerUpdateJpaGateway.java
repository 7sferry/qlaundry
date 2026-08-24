package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.update.CustomerUpdateGateway;
import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
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

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class CustomerUpdateJpaGateway implements CustomerUpdateGateway{
	private final CustomerJpaRepository customerJpaRepository;
	private final CustomerEmailJpaRepository customerEmailJpaRepository;
	private final CustomerPhoneJpaRepository customerPhoneJpaRepository;
	private final CustomerAddressJpaRepository customerAddressJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;
	private final IdGenerator idGenerator;
	private final CryptoTool cryptoTool;

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
	public CustomerEmailDomain save(CustomerEmailDomain email){
		String id = idGenerator.generateId();
		CustomerJpaEntity customer = customerJpaRepository.getReferenceById(email.customerId());
		CustomerEmailJpaEntity saved = customerEmailJpaRepository.save(
				CustomerEmailJpaEntity.construct(id, email, customer, cryptoTool));
		return CustomerEmailJpaEntity.construct(saved, cryptoTool);
	}

	@Override
	public CustomerPhoneDomain save(CustomerPhoneDomain phone){
		String id = idGenerator.generateId();
		CustomerJpaEntity customer = customerJpaRepository.getReferenceById(phone.customerId());
		CustomerPhoneJpaEntity saved = customerPhoneJpaRepository.save(
				CustomerPhoneJpaEntity.construct(id, phone, customer, cryptoTool));
		return CustomerPhoneJpaEntity.construct(saved, cryptoTool);
	}

	@Override
	public CustomerAddressDomain save(CustomerAddressDomain address){
		String id = idGenerator.generateId();
		CustomerJpaEntity customer = customerJpaRepository.getReferenceById(address.customerId());
		CustomerAddressJpaEntity saved = customerAddressJpaRepository.save(
				CustomerAddressJpaEntity.construct(id, address, customer, cryptoTool));
		return CustomerAddressJpaEntity.construct(saved, cryptoTool);
	}

	@Override
	public void deleteEmails(String customerId, String updatedBy){
		customerEmailJpaRepository.softDeleteByCustomerId(customerId, updatedBy);
	}

	@Override
	public void deletePhones(String customerId, String updatedBy){
		customerPhoneJpaRepository.softDeleteByCustomerId(customerId, updatedBy);
	}

	@Override
	public void deleteAddresses(String customerId, String updatedBy){
		customerAddressJpaRepository.softDeleteByCustomerId(customerId, updatedBy);
	}

}
