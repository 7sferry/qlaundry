package com.ferry.promotion.core.promotion.list;

import com.ferry.promotion.domain.promotion.PromotionDomain;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionListResponse(
	List<PromotionDomain> promotions,
	String nextCursor,
	String prevCursor){
}
