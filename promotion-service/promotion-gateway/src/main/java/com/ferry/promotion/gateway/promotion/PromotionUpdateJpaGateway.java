package com.ferry.promotion.gateway.promotion;

import com.ferry.promotion.core.promotion.update.PromotionUpdateGateway;
import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpaEntity;
import com.ferry.promotion.gateway.promotion.entity.PromotionTypeJpaEntity;
import com.ferry.promotion.gateway.promotion.repository.PromotionJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class PromotionUpdateJpaGateway implements PromotionUpdateGateway{
	private final PromotionJpaRepository promotionJpaRepository;

	@Override
	public Optional<PromotionDomain> findById(PromotionIdDomain promotionId, TenantIdDomain tenantId){
		return promotionJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(promotionId.value(), tenantId.value())
				.map(PromotionJpaEntity::construct);
	}

	@Override
	public boolean existsByCode(PromotionCodeDomain code, TenantIdDomain tenantId, PromotionIdDomain excludedId){
		return promotionJpaRepository.existsByCodeAndTenantIdAndIdNot(code.value(), tenantId.value(),
				excludedId.value());
	}

	@Override
	public PromotionDomain save(PromotionDomain promotion){
		PromotionJpaEntity saved = promotionJpaRepository.save(
				PromotionJpaEntity.construct(promotion.id(), promotion));
		return PromotionJpaEntity.construct(saved);
	}

}
