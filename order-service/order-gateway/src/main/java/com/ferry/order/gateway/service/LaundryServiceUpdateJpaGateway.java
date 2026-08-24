package com.ferry.order.gateway.service;

import com.ferry.order.core.service.update.LaundryServiceUpdateGateway;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceIdDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.gateway.service.entity.LaundryServiceJpaEntity;
import com.ferry.order.gateway.service.entity.ServiceCategoryJpaEntity;
import com.ferry.order.gateway.service.entity.ServiceUnitJpaEntity;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import com.ferry.order.gateway.service.repository.ServiceCategoryJpaRepository;
import com.ferry.order.gateway.service.repository.ServiceUnitJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class LaundryServiceUpdateJpaGateway implements LaundryServiceUpdateGateway{
	private final LaundryServiceJpaRepository laundryServiceJpaRepository;
	private final ServiceUnitJpaRepository serviceUnitJpaRepository;
	private final ServiceCategoryJpaRepository serviceCategoryJpaRepository;

	@Override
	public Optional<LaundryServiceDomain> findById(LaundryServiceIdDomain serviceId, TenantIdDomain tenantId){
		return laundryServiceJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(serviceId.value(), tenantId.value())
				.map(LaundryServiceJpaEntity::construct);
	}

	@Override
	public LaundryServiceDomain save(LaundryServiceDomain service){
		ServiceUnitJpaEntity unit = serviceUnitJpaRepository.getReferenceById(service.unit().getValue());
		ServiceCategoryJpaEntity category = serviceCategoryJpaRepository.getReferenceById(service.category().getValue());
		LaundryServiceJpaEntity saved = laundryServiceJpaRepository.save(
				LaundryServiceJpaEntity.construct(service.id(), service, unit, category));
		return LaundryServiceJpaEntity.construct(saved);
	}

}
