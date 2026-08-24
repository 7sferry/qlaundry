package com.ferry.order.gateway.service;

import com.ferry.order.core.service.create.LaundryServiceCreateGateway;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.gateway.service.entity.LaundryServiceJpaEntity;
import com.ferry.order.gateway.service.entity.ServiceCategoryJpaEntity;
import com.ferry.order.gateway.service.entity.ServiceUnitJpaEntity;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import com.ferry.order.gateway.service.repository.ServiceCategoryJpaRepository;
import com.ferry.order.gateway.service.repository.ServiceUnitJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class LaundryServiceCreateJpaGateway implements LaundryServiceCreateGateway{
	private final LaundryServiceJpaRepository laundryServiceJpaRepository;
	private final ServiceUnitJpaRepository serviceUnitJpaRepository;
	private final ServiceCategoryJpaRepository serviceCategoryJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public boolean existsByName(String name, TenantIdDomain tenantId){
		return laundryServiceJpaRepository.existsByNameIgnoreCaseAndTenantIdAndDeletedIsFalse(name, tenantId.value());
	}

	@Override
	public LaundryServiceDomain save(LaundryServiceDomain service){
		String id = idGenerator.generateId();
		ServiceUnitJpaEntity unit = serviceUnitJpaRepository.getReferenceById(service.unit().getValue());
		ServiceCategoryJpaEntity category = serviceCategoryJpaRepository.getReferenceById(service.category().getValue());
		LaundryServiceJpaEntity saved = laundryServiceJpaRepository.save(
				LaundryServiceJpaEntity.construct(id, service, unit, category));
		return LaundryServiceJpaEntity.construct(saved);
	}

}
