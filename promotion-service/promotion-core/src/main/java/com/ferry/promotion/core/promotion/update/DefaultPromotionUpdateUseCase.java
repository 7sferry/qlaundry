package com.ferry.promotion.core.promotion.update;

import com.ferry.promotion.domain.common.MoneyDomain;
import com.ferry.promotion.domain.common.NoteDomain;
import com.ferry.promotion.domain.common.exception.InvalidPromotionStateException;
import com.ferry.promotion.domain.common.exception.NotFoundException;
import com.ferry.promotion.domain.common.exception.PromotionForbiddenActionException;
import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.staff.StaffRole;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultPromotionUpdateUseCase implements PromotionUpdateUseCase{
	private final PromotionUpdateGateway gateway;

	@Override
	public void execute(PromotionUpdateRequest request, PromotionAuthPrincipal principal,
	                    PromotionUpdatePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new PromotionForbiddenActionException("Only super staff can manage promotions");
		}
		request.validate();
		PromotionIdDomain promotionId = new PromotionIdDomain(request.promotionId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		PromotionDomain promotion = gateway.findById(promotionId, tenantId)
				.orElseThrow(() -> new NotFoundException("Promotion Not Found"));
		PromotionCodeDomain code = new PromotionCodeDomain(request.code());
		if(gateway.existsByCode(code, tenantId, promotionId)){
			throw new InvalidPromotionStateException("Promotion code already exists");
		}
		boolean active = request.active() == null || request.active();
		boolean combinable = request.combinable() == null || request.combinable();
		PromotionDomain saved = gateway.save(promotion.update(code, request.name(),
				new NoteDomain(request.description()), request.type(), request.percentage(), money(request.amount()),
				money(request.maxDiscountAmount()), money(request.minSubtotal()), combinable, request.usageLimit(),
				instant(request.startAt()), instant(request.endAt()), active, principal.userId()));
		presenter.present(new PromotionUpdateResponse(saved));
	}

	private MoneyDomain money(BigDecimal value){
		return value == null ? null : new MoneyDomain(value);
	}

	private Instant instant(Long epochMilli){
		return epochMilli == null ? null : Instant.ofEpochMilli(epochMilli);
	}

}
