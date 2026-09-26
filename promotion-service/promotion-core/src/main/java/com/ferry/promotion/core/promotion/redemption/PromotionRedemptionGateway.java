package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.promotion.PromotionRedemption;
import com.ferry.promotion.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionRedemptionGateway{
	Optional<Promotion> findByCode(PromotionCode code, TenantId tenantId);

	Optional<Promotion> findById(PromotionId promotionId, TenantId tenantId);

	Optional<PromotionRedemption> findByReferenceId(String referenceId, String code, TenantId tenantId);

	boolean claimUsage(Promotion promotion);

	PromotionRedemption save(PromotionRedemption redemption);

	void rollback();
}
