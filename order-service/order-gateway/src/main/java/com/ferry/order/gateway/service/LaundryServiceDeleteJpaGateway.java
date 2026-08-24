package com.ferry.order.gateway.service;

import com.ferry.order.core.service.delete.LaundryServiceDeleteGateway;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceIdDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.service.entity.LaundryServiceJpaEntity;
import com.ferry.order.gateway.service.entity.ServiceCategoryJpaEntity;
import com.ferry.order.gateway.service.entity.ServiceUnitJpaEntity;
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
public class LaundryServiceDeleteJpaGateway implements LaundryServiceDeleteGateway{
	private static final List<Short> CLOSED_STATUS_IDS = Stream.of(OrderStatus.values())
			.filter(OrderStatus::isClosed)
			.map(OrderStatus::getValue)
			.toList();

	private final LaundryServiceJpaRepository laundryServiceJpaRepository;
	private final ServiceUnitJpaRepository serviceUnitJpaRepository;
	private final ServiceCategoryJpaRepository serviceCategoryJpaRepository;
	private final OrderJpaRepository orderJpaRepository;

	@Override
	public Optional<LaundryServiceDomain> findById(LaundryServiceIdDomain serviceId, TenantIdDomain tenantId){
		return laundryServiceJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(serviceId.value(), tenantId.value())
				.map(LaundryServiceJpaEntity::construct);
	}

	@Override
	public boolean hasOpenOrders(LaundryServiceIdDomain serviceId, TenantIdDomain tenantId){
		return orderJpaRepository.hasOpenOrders(serviceId.value(), tenantId.value(), CLOSED_STATUS_IDS);
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
