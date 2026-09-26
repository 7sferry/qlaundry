package com.ferry.promotion.gateway.promotion;

import com.ferry.promotion.core.promotion.detail.PromotionDetailGateway;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.tenant.TenantId;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpa;
import com.ferry.promotion.gateway.promotion.repository.PromotionJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaPromotionDetailGateway implements PromotionDetailGateway{
	private final PromotionJpaRepository promotionJpaRepository;

	@Override
	public Optional<Promotion> findById(PromotionId promotionId, TenantId tenantId){
		return promotionJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(promotionId.value(), tenantId.value())
				.map(PromotionJpa::construct);
	}

}
