package com.ferry.promotion.core.promotion.release;

import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.promotion.PromotionRedemption;
import com.ferry.promotion.domain.tenant.TenantId;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface PromotionReleaseGateway{
	List<PromotionRedemption> findByReferenceId(String referenceId, TenantId tenantId);

	boolean releaseUsage(PromotionId promotionId, TenantId tenantId, String releasedBy);

	PromotionRedemption save(PromotionRedemption redemption);
}
