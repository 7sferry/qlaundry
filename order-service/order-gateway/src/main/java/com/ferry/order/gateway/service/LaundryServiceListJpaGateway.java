package com.ferry.order.gateway.service;

import com.ferry.order.core.service.list.LaundryServiceListGateway;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.PaginationConstant;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortBy;
import com.ferry.utils.pagination.SortDirection;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceFilter;
import com.ferry.order.gateway.service.entity.LaundryServiceJpaEntity;
import com.ferry.order.gateway.service.repository.LaundryServiceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class LaundryServiceListJpaGateway implements LaundryServiceListGateway{
	private final LaundryServiceJpaRepository laundryServiceJpaRepository;

	@Override
	public CursorFetch<LaundryServiceDomain> findByFilter(LaundryServiceFilter filter){
		List<LaundryServiceJpaEntity> raw = fetchByFilter(filter);
		List<LaundryServiceDomain> rows = raw.stream().map(LaundryServiceJpaEntity::construct).toList();
		return CursorFetch.of(rows, PaginationConstant.PAGE_SIZE, filter.pageDirection());
	}

	private List<LaundryServiceJpaEntity> fetchByFilter(LaundryServiceFilter filter){
		Pageable pageable = PageRequest.ofSize(PaginationConstant.PAGE_SIZE + 1);
		boolean forward = filter.pageDirection() == PageDirection.NEXT;
		boolean ascending = filter.sortDir() == SortDirection.ASC;
		boolean useAfterQuery = forward == ascending;
		if(filter.sortBy() == SortBy.NAME){
			return useAfterQuery
					? laundryServiceJpaRepository.findAfterByName(filter, pageable)
					: laundryServiceJpaRepository.findBeforeByName(filter, pageable);
		}
		return useAfterQuery
				? laundryServiceJpaRepository.findAfterById(filter, pageable)
				: laundryServiceJpaRepository.findBeforeById(filter, pageable);
	}

}
