package com.ferry.promotion.core.promotion.toggle;

import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionToggleGateway{
	Optional<Promotion> findById(PromotionId promotionId, TenantId tenantId);

	Promotion save(Promotion promotion);
}
