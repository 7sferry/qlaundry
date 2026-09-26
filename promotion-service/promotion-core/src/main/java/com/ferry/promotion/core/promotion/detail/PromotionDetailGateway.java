package com.ferry.promotion.core.promotion.detail;

import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionDetailGateway{
	Optional<Promotion> findById(PromotionId promotionId, TenantId tenantId);
}
