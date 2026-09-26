package com.ferry.promotion.gateway.promotion;

import com.ferry.promotion.core.promotion.release.PromotionReleaseGateway;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.promotion.PromotionRedemption;
import com.ferry.promotion.domain.tenant.TenantId;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpa;
import com.ferry.promotion.gateway.promotion.entity.PromotionRedemptionJpa;
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
public class JpaPromotionReleaseGateway implements PromotionReleaseGateway{
	private final PromotionJpaRepository promotionJpaRepository;
	private final PromotionRedemptionJpaRepository promotionRedemptionJpaRepository;

	@Override
	public List<PromotionRedemption> findByReferenceId(String referenceId, TenantId tenantId){
		return promotionRedemptionJpaRepository
				.findAllByReferenceIdAndTenantIdAndDeletedIsFalse(referenceId, tenantId.value())
				.stream()
				.map(PromotionRedemptionJpa::construct)
				.toList();
	}

	@Override
	public boolean releaseUsage(PromotionId promotionId, TenantId tenantId, String releasedBy){
		return promotionJpaRepository.releaseUsage(promotionId.value(), tenantId.value(), releasedBy,
				Instant.now()) > 0;
	}

	@Override
	public PromotionRedemption save(PromotionRedemption redemption){
		PromotionJpa promotion = promotionJpaRepository.getReferenceById(redemption.promotionId());
		PromotionRedemptionJpa saved = promotionRedemptionJpaRepository.save(
				PromotionRedemptionJpa.construct(redemption.id(), redemption, promotion));
		return PromotionRedemptionJpa.construct(saved);
	}

}
