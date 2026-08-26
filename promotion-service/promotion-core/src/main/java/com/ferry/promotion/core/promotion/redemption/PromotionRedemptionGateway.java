package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.promotion.PromotionRedemptionDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionRedemptionGateway{
	Optional<PromotionDomain> findByCode(PromotionCodeDomain code, TenantIdDomain tenantId);

	Optional<PromotionDomain> findById(PromotionIdDomain promotionId, TenantIdDomain tenantId);

	Optional<PromotionRedemptionDomain> findByReferenceId(String referenceId, String code, TenantIdDomain tenantId);

	boolean claimUsage(PromotionDomain promotion);

	PromotionRedemptionDomain save(PromotionRedemptionDomain redemption);

	void rollback();
}
