package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.update.CustomerUpdateGateway;
import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.customer.CustomerPhone;
import com.ferry.user.domain.tenant.TenantId;
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

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaCustomerUpdateGateway implements CustomerUpdateGateway{
	private final CustomerJpaRepository customerJpaRepository;
	private final CustomerEmailJpaRepository customerEmailJpaRepository;
	private final CustomerPhoneJpaRepository customerPhoneJpaRepository;
	private final CustomerAddressJpaRepository customerAddressJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;
	private final IdGenerator idGenerator;
	private final CryptoTool cryptoTool;

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
	public CustomerEmail save(CustomerEmail email){
		String id = idGenerator.generateId();
		CustomerJpa customer = customerJpaRepository.getReferenceById(email.customerId());
		CustomerEmailJpa saved = customerEmailJpaRepository.save(
				CustomerEmailJpa.construct(id, email, customer, cryptoTool));
		return CustomerEmailJpa.construct(saved, cryptoTool);
	}

	@Override
	public CustomerPhone save(CustomerPhone phone){
		String id = idGenerator.generateId();
		CustomerJpa customer = customerJpaRepository.getReferenceById(phone.customerId());
		CustomerPhoneJpa saved = customerPhoneJpaRepository.save(
				CustomerPhoneJpa.construct(id, phone, customer, cryptoTool));
		return CustomerPhoneJpa.construct(saved, cryptoTool);
	}

	@Override
	public CustomerAddress save(CustomerAddress address){
		String id = idGenerator.generateId();
		CustomerJpa customer = customerJpaRepository.getReferenceById(address.customerId());
		CustomerAddressJpa saved = customerAddressJpaRepository.save(
				CustomerAddressJpa.construct(id, address, customer, cryptoTool));
		return CustomerAddressJpa.construct(saved, cryptoTool);
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
