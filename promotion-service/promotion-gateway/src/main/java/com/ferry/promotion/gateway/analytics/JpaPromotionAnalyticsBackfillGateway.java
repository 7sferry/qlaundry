package com.ferry.promotion.gateway.analytics;

import com.ferry.promotion.core.analytics.backfill.PromotionAnalyticsBackfillGateway;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpa;
import com.ferry.promotion.gateway.promotion.repository.PromotionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaPromotionAnalyticsBackfillGateway implements PromotionAnalyticsBackfillGateway{
	private final PromotionJpaRepository promotionJpaRepository;

	@Override
	public List<Promotion> findPromotionsAfter(String tenantId, String afterId, int limit){
		return promotionJpaRepository.findBackfillPage(tenantId, afterId, PageRequest.ofSize(limit)).stream()
				.map(PromotionJpa::construct)
				.toList();
	}

}
