package com.ferry.promotion.core.promotion.create;

import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionCreateGateway{
	boolean existsByCode(PromotionCodeDomain code, TenantIdDomain tenantId);

	PromotionDomain save(PromotionDomain promotion);
}
