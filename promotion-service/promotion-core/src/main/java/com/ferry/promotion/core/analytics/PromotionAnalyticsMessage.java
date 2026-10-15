package com.ferry.promotion.core.analytics;

import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionType;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PromotionAnalyticsMessage(
	String tenantId,
	String promotionId,
	String code,
	String name,
	PromotionType type,
	boolean combinable,
	Integer usageLimit,
	Long startAt,
	Long endAt,
	boolean active,
	boolean deleted,
	int version,
	Long createdAt,
	Long updatedAt){

	public static PromotionAnalyticsMessage from(Promotion promotion){
		return new PromotionAnalyticsMessage(promotion.tenantId(), promotion.id(), promotion.codeValue(),
				promotion.name(), promotion.type(), promotion.combinable(), promotion.usageLimit(),
				epochMillis(promotion.startAt()), epochMillis(promotion.endAt()), promotion.active(),
				promotion.deleted(), promotion.version() == null ? 0 : promotion.version(),
				epochMillis(promotion.createdAt()), epochMillis(promotion.updatedAt()));
	}

	private static Long epochMillis(Instant instant){
		return instant == null ? null : instant.toEpochMilli();
	}

}
