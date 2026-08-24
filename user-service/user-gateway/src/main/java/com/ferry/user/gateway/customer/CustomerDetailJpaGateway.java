package com.ferry.user.gateway.customer;

import com.ferry.user.core.customer.detail.CustomerDetailGateway;
import com.ferry.user.domain.customer.CustomerAddressDomain;
import com.ferry.user.domain.customer.CustomerAddressFilter;
import com.ferry.user.domain.customer.CustomerDomain;
import com.ferry.user.domain.customer.CustomerEmailDomain;
import com.ferry.user.domain.customer.CustomerEmailFilter;
import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.customer.CustomerPhoneDomain;
import com.ferry.user.domain.customer.CustomerPhoneFilter;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.gateway.customer.entity.CustomerAddressJpaEntity;
import com.ferry.user.gateway.customer.entity.CustomerEmailJpaEntity;
import com.ferry.user.gateway.customer.entity.CustomerJpaEntity;
import com.ferry.user.gateway.customer.entity.CustomerPhoneJpaEntity;
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
public class CustomerDetailJpaGateway implements CustomerDetailGateway{
	private final CustomerJpaRepository customerJpaRepository;
	private final CustomerEmailJpaRepository customerEmailJpaRepository;
	private final CustomerPhoneJpaRepository customerPhoneJpaRepository;
	private final CustomerAddressJpaRepository customerAddressJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<CustomerDomain> findById(CustomerIdDomain customerId, TenantIdDomain tenantId){
		return customerJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(customerId.value(), tenantId.value())
				.map(CustomerJpaEntity::construct);
	}

	@Override
	public List<CustomerEmailDomain> findEmailsByFilter(CustomerEmailFilter filter){
		return customerEmailJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> CustomerEmailJpaEntity.construct(entity, cryptoTool))
				.toList();
	}

	@Override
	public List<CustomerPhoneDomain> findPhonesByFilter(CustomerPhoneFilter filter){
		return customerPhoneJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> CustomerPhoneJpaEntity.construct(entity, cryptoTool))
				.toList();
	}

	@Override
	public List<CustomerAddressDomain> findAddressesByFilter(CustomerAddressFilter filter){
		return customerAddressJpaRepository.findAllWithFilter(filter).stream()
				.map(entity -> CustomerAddressJpaEntity.construct(entity, cryptoTool))
				.toList();
	}

}
