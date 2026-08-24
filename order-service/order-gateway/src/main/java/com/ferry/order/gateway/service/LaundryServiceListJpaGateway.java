package com.ferry.order.gateway.service;

import com.ferry.order.core.service.list.LaundryServiceListGateway;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceFilter;
import com.ferry.order.gateway.service.entity.LaundryServiceJpaEntity;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class LaundryServiceListJpaGateway implements LaundryServiceListGateway{
	private final LaundryServiceJpaRepository laundryServiceJpaRepository;

	@Override
	public List<LaundryServiceDomain> findByFilter(LaundryServiceFilter filter){
		return laundryServiceJpaRepository.findAllWithFilter(filter).stream()
				.map(LaundryServiceJpaEntity::construct)
				.toList();
	}

}
