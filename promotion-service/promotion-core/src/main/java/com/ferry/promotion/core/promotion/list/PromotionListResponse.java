package com.ferry.promotion.core.promotion.list;

import com.ferry.promotion.domain.promotion.Promotion;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionListResponse(
	List<Promotion> promotions,
	String nextCursor,
	String prevCursor){
}
