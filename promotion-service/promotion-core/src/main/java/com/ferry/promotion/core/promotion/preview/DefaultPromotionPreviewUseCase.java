package com.ferry.promotion.core.promotion.preview;

import com.ferry.promotion.core.promotion.redemption.DiscountCalculator;
import com.ferry.promotion.core.promotion.redemption.DiscountStrategy;
import com.ferry.promotion.core.promotion.redemption.DiscountStrategyFactory;
import com.ferry.promotion.domain.common.Money;
import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionRejection;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public class DefaultPromotionPreviewUseCase implements PromotionPreviewUseCase{

	@Override
	public void execute(PromotionPreviewRequest request, PromotionAuthPrincipal principal,
	                    PromotionPreviewPresenter presenter){
		request.validate();
		Money subtotal = new Money(request.subtotal());
		boolean multipleCodes = request.promotions().size() > 1;
		DiscountCalculator calculator = new DiscountCalculator(subtotal);
		List<PromotionPreviewResult> results = new ArrayList<>(request.promotions().size());
		for(PromotionSnapshot snapshot : request.promotions()){
			Promotion promotion = toDomain(snapshot, principal.tenantId());
			results.add(preview(promotion, subtotal, multipleCodes, calculator));
		}
		presenter.present(new PromotionPreviewResponse(results));
	}

	private PromotionPreviewResult preview(Promotion promotion, Money subtotal, boolean multipleCodes,
	                                       DiscountCalculator calculator){
		PromotionRejection rejection = promotion.rejectionFor(Instant.now(), subtotal, multipleCodes).orElse(null);
		if(rejection != null){
			return new PromotionPreviewResult(promotion.codeValue(), promotion, null, rejection);
		}
		DiscountStrategy discountStrategy = DiscountStrategyFactory.from(promotion);
		Money discount = discountStrategy.calculate(calculator);
		if(!discount.isPositive()){
			return new PromotionPreviewResult(promotion.codeValue(), promotion, null, PromotionRejection.NO_DISCOUNT);
		}
		return new PromotionPreviewResult(promotion.codeValue(), promotion, discount, null);
	}

	private Promotion toDomain(PromotionSnapshot snapshot, String tenantId){
		return Promotion.builder()
				.id(snapshot.id())
				.tenantId(tenantId)
				.code(new PromotionCode(snapshot.code()))
				.name(snapshot.name())
				.type(snapshot.type())
				.percentage(snapshot.percentage())
				.amount(money(snapshot.amount()))
				.maxDiscountAmount(money(snapshot.maxDiscountAmount()))
				.minSubtotal(money(snapshot.minSubtotal()))
				.combinable(snapshot.combinable())
				.usageLimit(snapshot.usageLimit())
				.usedCount(snapshot.usedCount())
				.active(snapshot.active())
				.startAt(Instant.ofEpochMilli(snapshot.startAt()))
				.endAt(Instant.ofEpochMilli(snapshot.endAt()))
				.build();
	}

	private Money money(BigDecimal value){
		return value == null ? null : new Money(value);
	}

}
