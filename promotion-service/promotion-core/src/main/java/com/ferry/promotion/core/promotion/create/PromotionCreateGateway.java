package com.ferry.promotion.core.promotion.create;

import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.tenant.TenantId;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionCreateGateway{
	boolean existsByCode(PromotionCode code, TenantId tenantId);

	Promotion save(Promotion promotion);
}
