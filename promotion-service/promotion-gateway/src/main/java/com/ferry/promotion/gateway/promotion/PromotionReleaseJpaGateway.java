package com.ferry.promotion.gateway.promotion;

import com.ferry.promotion.core.promotion.release.PromotionReleaseGateway;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.promotion.PromotionRedemptionDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpaEntity;
import com.ferry.promotion.gateway.promotion.entity.PromotionRedemptionJpaEntity;
import com.ferry.promotion.gateway.promotion.repository.PromotionJpaRepository;
import com.ferry.promotion.gateway.promotion.repository.PromotionRedemptionJpaRepository;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class PromotionReleaseJpaGateway implements PromotionReleaseGateway{
	private final PromotionJpaRepository promotionJpaRepository;
	private final PromotionRedemptionJpaRepository promotionRedemptionJpaRepository;

	@Override
	public List<PromotionRedemptionDomain> findByReferenceId(String referenceId, TenantIdDomain tenantId){
		return promotionRedemptionJpaRepository
				.findAllByReferenceIdAndTenantIdAndDeletedIsFalse(referenceId, tenantId.value())
				.stream()
				.map(PromotionRedemptionJpaEntity::construct)
				.toList();
	}

	@Override
	public boolean releaseUsage(PromotionIdDomain promotionId, TenantIdDomain tenantId, String releasedBy){
		return promotionJpaRepository.releaseUsage(promotionId.value(), tenantId.value(), releasedBy,
				Instant.now()) > 0;
	}

	@Override
	public PromotionRedemptionDomain save(PromotionRedemptionDomain redemption){
		PromotionJpaEntity promotion = promotionJpaRepository.getReferenceById(redemption.promotionId());
		PromotionRedemptionJpaEntity saved = promotionRedemptionJpaRepository.save(
				PromotionRedemptionJpaEntity.construct(redemption.id(), redemption, promotion));
		return PromotionRedemptionJpaEntity.construct(saved);
	}

}
