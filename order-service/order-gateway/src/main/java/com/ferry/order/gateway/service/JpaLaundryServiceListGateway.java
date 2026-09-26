package com.ferry.order.gateway.service;

import com.ferry.order.core.service.list.LaundryServiceListGateway;
import com.ferry.order.domain.service.ServiceListSortBy;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortDirection;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.LaundryServiceFilter;
import com.ferry.order.gateway.service.entity.LaundryServiceJpa;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaLaundryServiceListGateway implements LaundryServiceListGateway{
	private final LaundryServiceJpaRepository laundryServiceJpaRepository;

	@Override
	public CursorFetch<LaundryService> findByFilter(LaundryServiceFilter filter){
		List<LaundryServiceJpa> raw = fetchByFilter(filter);
		List<LaundryService> rows = raw.stream().map(LaundryServiceJpa::construct).toList();
		return CursorFetch.of(rows, filter.pageSize(), filter.pageDirection());
	}

	private List<LaundryServiceJpa> fetchByFilter(LaundryServiceFilter filter){
		Pageable pageable = PageRequest.ofSize(filter.pageSize() + 1);
		boolean forward = filter.pageDirection() == PageDirection.NEXT;
		boolean ascending = filter.sortDir() == SortDirection.ASC;
		boolean useAfterQuery = forward == ascending;
		if(filter.sortBy() == ServiceListSortBy.NAME){
			return useAfterQuery
					? laundryServiceJpaRepository.findAfterByName(filter, pageable)
					: laundryServiceJpaRepository.findBeforeByName(filter, pageable);
		}
		return useAfterQuery
				? laundryServiceJpaRepository.findAfterById(filter, pageable)
				: laundryServiceJpaRepository.findBeforeById(filter, pageable);
	}

}
