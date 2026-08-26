package com.ferry.order.domain.order;

import com.ferry.order.domain.common.MoneyDomain;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record OrderPromotionDomain(String id, String orderId, String promotionId, String code,
                                   MoneyDomain discountAmount, Integer version, boolean deleted, Instant createdAt,
                                   String createdBy, Instant updatedAt, String updatedBy){
	public OrderPromotionDomain{
		if(orderId == null || orderId.isBlank()){
			throw new IllegalArgumentException("Order id must not be blank");
		}
		if(promotionId == null || promotionId.isBlank()){
			throw new IllegalArgumentException("Promotion id must not be blank");
		}
		if(code == null || code.isBlank()){
			throw new IllegalArgumentException("Promotion code must not be blank");
		}
		if(discountAmount == null){
			throw new IllegalArgumentException("Promotion discount amount must not be null");
		}
	}

	public static OrderPromotionDomain register(String orderId, String promotionId, String code,
	                                            MoneyDomain discountAmount, String createdBy){
		Instant now = Instant.now();
		return new OrderPromotionDomain(null, orderId, promotionId, code, discountAmount, null, false, now,
				createdBy, now, createdBy);
	}

}
