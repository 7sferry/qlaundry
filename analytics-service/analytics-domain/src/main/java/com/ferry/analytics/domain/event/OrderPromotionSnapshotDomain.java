package com.ferry.analytics.domain.event;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Builder(toBuilder = true)
public record OrderPromotionSnapshotDomain(String tenantId, String orderId, String promotionId, String code,
                                           BigDecimal discountAmount, boolean deleted, int version,
                                           Instant createdAt){
	public OrderPromotionSnapshotDomain{
		if(tenantId == null || tenantId.isBlank() || orderId == null || orderId.isBlank()){
			throw new InvalidAnalyticStateException("Promotion tenant id and order id must not be blank");
		}
		if(promotionId == null || promotionId.isBlank()){
			throw new InvalidAnalyticStateException("Promotion id must not be blank");
		}
		if(version < 0){
			throw new InvalidAnalyticStateException("Promotion version must not be negative");
		}
	}
}
