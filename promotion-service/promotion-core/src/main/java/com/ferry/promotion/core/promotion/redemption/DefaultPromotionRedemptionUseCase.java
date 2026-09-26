package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.common.Money;
import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.promotion.PromotionRedemption;
import com.ferry.promotion.domain.promotion.PromotionRejection;
import com.ferry.promotion.domain.tenant.TenantId;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultPromotionRedemptionUseCase implements PromotionRedemptionUseCase{
	private final PromotionRedemptionGateway gateway;

	@Override
	public void execute(PromotionRedemptionRequest request, PromotionRedemptionPresenter presenter){
		request.validate();
		TenantId tenantId = new TenantId(request.tenantId());
		Money subtotal = new Money(request.subtotal());
		boolean multipleCodes = request.codes().size() > 1;
		List<PromotionRedemptionResponse> responses = new ArrayList<>(request.codes().size());
		DiscountCalculator calculator = new DiscountCalculator(subtotal);
		for(String code : request.codes()){
			PromotionRedemptionResponse response = redeem(code, tenantId, subtotal, multipleCodes,
					request, calculator);
			responses.add(response);
		}
		boolean anyFailed = responses.stream().anyMatch(o -> !o.isApplied());
		if(anyFailed){
			gateway.rollback();
		}
		presenter.present(responses);
	}

	private PromotionRedemptionResponse redeem(String code, TenantId tenantId, Money subtotal,
	                                           boolean multipleCodes,
	                                           PromotionRedemptionRequest request, DiscountCalculator calculator){
		PromotionRedemption replayed = gateway.findByReferenceId(request.referenceId(), code, tenantId)
				.orElse(null);
		if(replayed != null){
			Promotion promotion = gateway.findById(new PromotionId(replayed.promotionId()), tenantId)
					.orElse(null);
			return new PromotionRedemptionResponse(code, promotion, replayed, null);
		}
		PromotionCode promotionCode = new PromotionCode(code);
		Promotion promotion = gateway.findByCode(promotionCode, tenantId).orElse(null);
		if(promotion == null){
			return new PromotionRedemptionResponse(code, null, null, PromotionRejection.NOT_FOUND);
		}
		PromotionRejection rejection = promotion.rejectionFor(Instant.now(), subtotal, multipleCodes).orElse(null);
		if(rejection != null){
			return new PromotionRedemptionResponse(code, promotion, null, rejection);
		}
		Promotion redeemed = promotion.redeem(request.redeemedBy());
		if(!gateway.claimUsage(redeemed)){
			return new PromotionRedemptionResponse(code, promotion, null, PromotionRejection.EXHAUSTED);
		}
		DiscountStrategy discountStrategy = DiscountStrategyFactory.from(promotion);
		Money discount = discountStrategy.calculate(calculator);
		if(!discount.isPositive()){
			return new PromotionRedemptionResponse(code, promotion, null, PromotionRejection.NO_DISCOUNT);
		}
		PromotionRedemption redemption = gateway.save(PromotionRedemption.register(redeemed,
				request.referenceId(), request.customerId(), subtotal, discount, request.redeemedBy()));
		return new PromotionRedemptionResponse(code, redeemed, redemption, null);
	}

}
