package com.ferry.order.gateway.service;

import com.ferry.order.core.service.update.LaundryServiceUpdateGateway;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.LaundryServiceId;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.gateway.service.entity.LaundryServiceJpa;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaLaundryServiceUpdateGateway implements LaundryServiceUpdateGateway{
	private final LaundryServiceJpaRepository laundryServiceJpaRepository;

	@Override
	public Optional<LaundryService> findById(LaundryServiceId serviceId, TenantId tenantId){
		return laundryServiceJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(serviceId.value(), tenantId.value())
				.map(LaundryServiceJpa::construct);
	}

	@Override
	public LaundryService save(LaundryService service){
		LaundryServiceJpa saved = laundryServiceJpaRepository.saveAndFlush(
				LaundryServiceJpa.construct(service.id(), service));
		return LaundryServiceJpa.construct(saved);
	}

}
