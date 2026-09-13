package com.ferry.promotion.core.promotion.release;

import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.promotion.PromotionRedemptionDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface PromotionReleaseGateway{
	List<PromotionRedemptionDomain> findByReferenceId(String referenceId, TenantIdDomain tenantId);

	boolean releaseUsage(PromotionIdDomain promotionId, TenantIdDomain tenantId, String releasedBy);

	PromotionRedemptionDomain save(PromotionRedemptionDomain redemption);
}
