package com.ferry.promotion.core.promotion.detail;

import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionDetailGateway{
	Optional<PromotionDomain> findById(PromotionIdDomain promotionId, TenantIdDomain tenantId);
}
