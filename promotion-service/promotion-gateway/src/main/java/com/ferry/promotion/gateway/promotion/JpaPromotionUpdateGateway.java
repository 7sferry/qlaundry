package com.ferry.promotion.gateway.promotion;

import com.ferry.promotion.core.promotion.update.PromotionUpdateGateway;
import com.ferry.promotion.domain.promotion.PromotionCode;
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
public class JpaPromotionUpdateGateway implements PromotionUpdateGateway{
	private final PromotionJpaRepository promotionJpaRepository;

	@Override
	public Optional<Promotion> findById(PromotionId promotionId, TenantId tenantId){
		return promotionJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(promotionId.value(), tenantId.value())
				.map(PromotionJpa::construct);
	}

	@Override
	public boolean existsByCode(PromotionCode code, TenantId tenantId, PromotionId excludedId){
		return promotionJpaRepository.existsByCodeAndTenantIdAndIdNot(code.value(), tenantId.value(),
				excludedId.value());
	}

	@Override
	public Promotion save(Promotion promotion){
		PromotionJpa saved = promotionJpaRepository.save(
				PromotionJpa.construct(promotion.id(), promotion));
		return PromotionJpa.construct(saved);
	}

}
