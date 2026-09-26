package com.ferry.order.gateway.service;

import com.ferry.order.core.service.create.LaundryServiceCreateGateway;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.gateway.service.entity.LaundryServiceJpa;
import com.ferry.order.gateway.service.entity.ServiceCategoryJpa;
import com.ferry.order.gateway.service.entity.ServiceUnitJpa;
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
public class JpaLaundryServiceCreateGateway implements LaundryServiceCreateGateway{
	private final LaundryServiceJpaRepository laundryServiceJpaRepository;
	private final ServiceUnitJpaRepository serviceUnitJpaRepository;
	private final ServiceCategoryJpaRepository serviceCategoryJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public boolean existsByName(String name, TenantId tenantId){
		return laundryServiceJpaRepository.existsByNameIgnoreCaseAndTenantIdAndDeletedIsFalse(name, tenantId.value());
	}

	@Override
	public LaundryService save(LaundryService service){
		String id = idGenerator.generateId();
		ServiceUnitJpa unit = serviceUnitJpaRepository.getReferenceById(service.unit().getValue());
		ServiceCategoryJpa category = serviceCategoryJpaRepository.getReferenceById(service.category().getValue());
		LaundryServiceJpa saved = laundryServiceJpaRepository.save(
				LaundryServiceJpa.construct(id, service, unit, category));
		return LaundryServiceJpa.construct(saved);
	}

}
