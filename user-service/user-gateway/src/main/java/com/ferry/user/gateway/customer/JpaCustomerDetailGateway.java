package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.detail.CustomerDetailGateway;
import com.ferry.user.domain.customer.CustomerAddress;
import com.ferry.user.domain.customer.CustomerAddressFilter;
import com.ferry.user.domain.customer.Customer;
import com.ferry.user.domain.customer.CustomerEmail;
import com.ferry.user.domain.customer.CustomerEmailFilter;
import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.customer.CustomerPhone;
import com.ferry.user.domain.customer.CustomerPhoneFilter;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.gateway.customer.entity.CustomerAddressJpa;
import com.ferry.user.gateway.customer.entity.CustomerEmailJpa;
import com.ferry.user.gateway.customer.entity.CustomerJpa;
import com.ferry.user.gateway.customer.entity.CustomerPhoneJpa;
import com.ferry.user.gateway.customer.repository.CustomerAddressJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerEmailJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerJpaRepository;
import com.ferry.user.gateway.customer.repository.CustomerPhoneJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaCustomerDetailGateway implements CustomerDetailGateway{
	private final CustomerJpaRepository customerJpaRepository;
	private final CustomerEmailJpaRepository customerEmailJpaRepository;
	private final CustomerPhoneJpaRepository customerPhoneJpaRepository;
	private final CustomerAddressJpaRepository customerAddressJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<Customer> findById(CustomerId customerId, TenantId tenantId){
		return customerJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(customerId.value(), tenantId.value())
				.map(CustomerJpa::construct);
	}

	@Override
	public List<CustomerEmail> findEmailsByFilter(CustomerEmailFilter filter){
		return customerEmailJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> CustomerEmailJpa.construct(entity, cryptoTool))
				.toList();
	}

	@Override
	public List<CustomerPhone> findPhonesByFilter(CustomerPhoneFilter filter){
		return customerPhoneJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> CustomerPhoneJpa.construct(entity, cryptoTool))
				.toList();
	}

	@Override
	public List<CustomerAddress> findAddressesByFilter(CustomerAddressFilter filter){
		return customerAddressJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> CustomerAddressJpa.construct(entity, cryptoTool))
				.toList();
	}

}
