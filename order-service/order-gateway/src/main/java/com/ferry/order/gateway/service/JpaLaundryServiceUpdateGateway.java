package com.ferry.order.gateway.service;

import com.ferry.order.core.service.update.LaundryServiceUpdateGateway;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.LaundryServiceId;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.gateway.service.entity.LaundryServiceJpa;
import com.ferry.order.gateway.service.entity.ServiceCategoryJpa;
import com.ferry.order.gateway.service.entity.ServiceUnitJpa;
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
public class JpaLaundryServiceUpdateGateway implements LaundryServiceUpdateGateway{
	private final LaundryServiceJpaRepository laundryServiceJpaRepository;
	private final ServiceUnitJpaRepository serviceUnitJpaRepository;
	private final ServiceCategoryJpaRepository serviceCategoryJpaRepository;

	@Override
	public Optional<LaundryService> findById(LaundryServiceId serviceId, TenantId tenantId){
		return laundryServiceJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(serviceId.value(), tenantId.value())
				.map(LaundryServiceJpa::construct);
	}

	@Override
	public LaundryService save(LaundryService service){
		ServiceUnitJpa unit = serviceUnitJpaRepository.getReferenceById(service.unit().getValue());
		ServiceCategoryJpa category = serviceCategoryJpaRepository.getReferenceById(service.category().getValue());
		LaundryServiceJpa saved = laundryServiceJpaRepository.saveAndFlush(
				LaundryServiceJpa.construct(service.id(), service, unit, category));
		return LaundryServiceJpa.construct(saved);
	}

}
