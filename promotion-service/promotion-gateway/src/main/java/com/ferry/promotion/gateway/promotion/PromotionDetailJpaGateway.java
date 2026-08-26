package com.ferry.promotion.gateway.promotion;

import com.ferry.promotion.core.promotion.detail.PromotionDetailGateway;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpaEntity;
import com.ferry.promotion.gateway.promotion.repository.PromotionJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class PromotionDetailJpaGateway implements PromotionDetailGateway{
	private final PromotionJpaRepository promotionJpaRepository;

	@Override
	public Optional<PromotionDomain> findById(PromotionIdDomain promotionId, TenantIdDomain tenantId){
		return promotionJpaRepository.findByIdAndTenantIdAndDeletedIsFalse(promotionId.value(), tenantId.value())
				.map(PromotionJpaEntity::construct);
	}

}
