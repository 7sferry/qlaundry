package com.ferry.promotion.core.promotion.list;

import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionFilter;
import com.ferry.utils.pagination.CursorFetch;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionListGateway{
	CursorFetch<Promotion> findByFilter(PromotionFilter filter);
}
