package com.ferry.promotion.gateway.promotion;

import com.ferry.promotion.core.promotion.list.PromotionListGateway;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionFilter;
import com.ferry.promotion.domain.promotion.PromotionListSortBy;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpa;
import com.ferry.promotion.gateway.promotion.repository.PromotionJpaRepository;
import com.ferry.utils.pagination.CursorFetch;
import com.ferry.utils.pagination.PageDirection;
import com.ferry.utils.pagination.SortDirection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaPromotionListGateway implements PromotionListGateway{
	private final PromotionJpaRepository promotionJpaRepository;

	@Override
	public CursorFetch<Promotion> findByFilter(PromotionFilter filter){
		List<PromotionJpa> raw = fetchByFilter(filter);
		List<Promotion> rows = raw.stream().map(PromotionJpa::construct).toList();
		return CursorFetch.of(rows, filter.pageSize(), filter.pageDirection());
	}

	private List<PromotionJpa> fetchByFilter(PromotionFilter filter){
		Pageable pageable = PageRequest.ofSize(filter.pageSize() + 1);
		boolean forward = filter.pageDirection() == PageDirection.NEXT;
		boolean ascending = filter.sortDir() == SortDirection.ASC;
		boolean useAfterQuery = forward == ascending;
		if(filter.sortBy() == PromotionListSortBy.NAME){
			return useAfterQuery
					? promotionJpaRepository.findAfterByName(filter, pageable)
					: promotionJpaRepository.findBeforeByName(filter, pageable);
		}
		if(filter.sortBy() == PromotionListSortBy.END_AT){
			return useAfterQuery
					? promotionJpaRepository.findAfterByEndAt(filter, pageable)
					: promotionJpaRepository.findBeforeByEndAt(filter, pageable);
		}
		if(filter.sortBy() == PromotionListSortBy.CODE){
			return useAfterQuery
					? promotionJpaRepository.findAfterByCode(filter, pageable)
					: promotionJpaRepository.findBeforeByCode(filter, pageable);
		}
		return useAfterQuery
				? promotionJpaRepository.findAfterById(filter, pageable)
				: promotionJpaRepository.findBeforeById(filter, pageable);
	}

}
