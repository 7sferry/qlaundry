package com.ferry.promotion.core.promotion.update;

import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionUpdateGateway{
	Optional<Promotion> findById(PromotionId promotionId, TenantId tenantId);

	boolean existsByCode(PromotionCode code, TenantId tenantId, PromotionId excludedId);

	Promotion save(Promotion promotion);
}
