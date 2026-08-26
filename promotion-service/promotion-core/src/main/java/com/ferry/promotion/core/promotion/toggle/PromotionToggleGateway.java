package com.ferry.promotion.core.promotion.toggle;

import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionToggleGateway{
	Optional<PromotionDomain> findById(PromotionIdDomain promotionId, TenantIdDomain tenantId);

	PromotionDomain save(PromotionDomain promotion);
}
