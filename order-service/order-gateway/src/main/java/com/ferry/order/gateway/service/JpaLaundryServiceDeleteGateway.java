package com.ferry.order.gateway.service;

import com.ferry.order.core.service.delete.LaundryServiceDeleteGateway;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.LaundryServiceId;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.service.entity.LaundryServiceJpa;
import com.ferry.order.gateway.service.entity.ServiceCategoryJpa;
import com.ferry.order.gateway.service.entity.ServiceUnitJpa;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import com.ferry.order.gateway.service.repository.ServiceCategoryJpaRepository;
import com.ferry.order.gateway.service.repository.ServiceUnitJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaLaundryServiceDeleteGateway implements LaundryServiceDeleteGateway{
	private static final List<Short> CLOSED_STATUS_IDS = Stream.of(OrderStatus.values())
			.filter(OrderStatus::isClosed)
			.map(OrderStatus::getValue)
			.toList();

	private final LaundryServiceJpaRepository laundryServiceJpaRepository;
	private final ServiceUnitJpaRepository serviceUnitJpaRepository;
	private final ServiceCategoryJpaRepository serviceCategoryJpaRepository;
	private final OrderJpaRepository orderJpaRepository;

	@Override
	public Optional<LaundryService> findById(LaundryServiceId serviceId, TenantId tenantId){
		return laundryServiceJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(serviceId.value(), tenantId.value())
				.map(LaundryServiceJpa::construct);
	}

	@Override
	public boolean hasOpenOrders(LaundryServiceId serviceId, TenantId tenantId){
		return orderJpaRepository.hasOpenOrders(serviceId.value(), tenantId.value(), CLOSED_STATUS_IDS);
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
