package com.ferry.promotion.core.promotion.update;

import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionUpdateGateway{
	Optional<PromotionDomain> findById(PromotionIdDomain promotionId, TenantIdDomain tenantId);

	boolean existsByCode(PromotionCodeDomain code, TenantIdDomain tenantId, PromotionIdDomain excludedId);

	PromotionDomain save(PromotionDomain promotion);
}
