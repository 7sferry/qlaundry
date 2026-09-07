package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.common.MoneyDomain;
import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.promotion.PromotionRedemptionDomain;
import com.ferry.promotion.domain.promotion.PromotionRejection;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
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
		TenantIdDomain tenantId = new TenantIdDomain(request.tenantId());
		MoneyDomain subtotal = new MoneyDomain(request.subtotal());
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

	private PromotionRedemptionResponse redeem(String code, TenantIdDomain tenantId, MoneyDomain subtotal,
	                                           boolean multipleCodes,
	                                           PromotionRedemptionRequest request, DiscountCalculator calculator){
		PromotionRedemptionDomain replayed = gateway.findByReferenceId(request.referenceId(), code, tenantId)
				.orElse(null);
		if(replayed != null){
			PromotionDomain promotion = gateway.findById(new PromotionIdDomain(replayed.promotionId()), tenantId)
					.orElse(null);
			return new PromotionRedemptionResponse(code, promotion, replayed, null);
		}
		PromotionCodeDomain promotionCode = new PromotionCodeDomain(code);
		PromotionDomain promotion = gateway.findByCode(promotionCode, tenantId).orElse(null);
		if(promotion == null){
			return new PromotionRedemptionResponse(code, null, null, PromotionRejection.NOT_FOUND);
		}
		PromotionRejection rejection = promotion.rejectionFor(Instant.now(), subtotal, multipleCodes).orElse(null);
		if(rejection != null){
			return new PromotionRedemptionResponse(code, promotion, null, rejection);
		}
		PromotionDomain redeemed = promotion.redeem(request.redeemedBy());
		if(!gateway.claimUsage(redeemed)){
			return new PromotionRedemptionResponse(code, promotion, null, PromotionRejection.EXHAUSTED);
		}
		DiscountStrategy discountStrategy = getDiscountStrategy(promotion);
		MoneyDomain discount = discountStrategy.calculate(calculator);
		if(!discount.isPositive()){
			return new PromotionRedemptionResponse(code, promotion, null, PromotionRejection.NO_DISCOUNT);
		}
		PromotionRedemptionDomain redemption = gateway.save(PromotionRedemptionDomain.register(redeemed,
				request.referenceId(), request.customerId(), subtotal, discount, request.redeemedBy()));
		return new PromotionRedemptionResponse(code, redeemed, redemption, null);
	}

	public DiscountStrategy getDiscountStrategy(PromotionDomain promotion){
		return switch(promotion.type()){
			case PERCENTAGE -> new PercentageDiscount(BigDecimal.valueOf(promotion.percentage()), promotion.maxDiscountAmount());
			case NON_CUMULATIVE_PERCENTAGE -> new NonCumulativePercentageDiscount(BigDecimal.valueOf(promotion.percentage()), promotion.maxDiscountAmount());
			case FIXED_AMOUNT -> new AmountDiscount(promotion.amountValue(), promotion.maxDiscountAmount());
		};
	}

}
