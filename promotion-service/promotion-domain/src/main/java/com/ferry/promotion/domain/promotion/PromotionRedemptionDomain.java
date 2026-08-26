package com.ferry.promotion.domain.promotion;

import com.ferry.promotion.domain.common.MoneyDomain;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record PromotionRedemptionDomain(String id, String promotionId, String tenantId, PromotionCodeDomain code,
                                        String referenceId, String customerId, MoneyDomain subtotal,
                                        MoneyDomain discountAmount, Integer version, boolean deleted,
                                        Instant createdAt, String createdBy, Instant updatedAt, String updatedBy){
	public PromotionRedemptionDomain{
		if(promotionId == null || promotionId.isBlank()){
			throw new IllegalArgumentException("Promotion id must not be blank");
		}
		if(tenantId == null || tenantId.isBlank()){
			throw new IllegalArgumentException("Tenant id must not be blank");
		}
		if(referenceId == null || referenceId.isBlank()){
			throw new IllegalArgumentException("Reference id must not be blank");
		}
		if(code == null || subtotal == null || discountAmount == null){
			throw new IllegalArgumentException("Code, subtotal and discount amount must not be null");
		}
	}

	public static PromotionRedemptionDomain register(PromotionDomain promotion, String referenceId, String customerId,
	                                                 MoneyDomain subtotal, MoneyDomain discountAmount,
	                                                 String createdBy){
		Instant now = Instant.now();
		return new PromotionRedemptionDomain(null, promotion.id(), promotion.tenantId(), promotion.code(),
				referenceId, customerId, subtotal, discountAmount, null, false, now, createdBy, now, createdBy);
	}

	public String codeValue(){
		return code.value();
	}

}
