package com.ferry.promotion.gateway.promotion;

import com.ferry.promotion.core.promotion.toggle.PromotionToggleGateway;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.tenant.TenantId;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpa;
import com.ferry.promotion.gateway.promotion.entity.PromotionTypeJpa;
import com.ferry.promotion.gateway.promotion.repository.PromotionJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaPromotionToggleGateway implements PromotionToggleGateway{
	private final PromotionJpaRepository promotionJpaRepository;

	@Override
	public Optional<Promotion> findById(PromotionId promotionId, TenantId tenantId){
		return promotionJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(promotionId.value(), tenantId.value())
				.map(PromotionJpa::construct);
	}

	@Override
	public Promotion save(Promotion promotion){
		PromotionJpa saved = promotionJpaRepository.save(
				PromotionJpa.construct(promotion.id(), promotion));
		return PromotionJpa.construct(saved);
	}

}
