package com.ferry.promotion.core.promotion.create;

import com.ferry.promotion.domain.common.MoneyDomain;
import com.ferry.promotion.domain.common.NoteDomain;
import com.ferry.promotion.domain.common.exception.InvalidPromotionStateException;
import com.ferry.promotion.domain.common.exception.PromotionForbiddenActionException;
import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
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
public class DefaultPromotionCreateUseCase implements PromotionCreateUseCase{
	private final PromotionCreateGateway gateway;

	@Override
	public void execute(PromotionCreateRequest request, PromotionAuthPrincipal principal,
	                    PromotionCreatePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new PromotionForbiddenActionException("Only super staff can manage promotions");
		}
		request.validate();
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		PromotionCodeDomain code = new PromotionCodeDomain(request.code());
		if(gateway.existsByCode(code, tenantId)){
			throw new InvalidPromotionStateException("Promotion code already exists");
		}
		boolean combinable = request.combinable() == null || request.combinable();
		PromotionDomain saved = gateway.save(PromotionDomain.create(tenantId.value(), code, request.name(),
				new NoteDomain(request.description()), request.type(), request.percentage(),
				money(request.amount()), money(request.maxDiscountAmount()), money(request.minSubtotal()),
				combinable, request.usageLimit(), instant(request.startAt()), instant(request.endAt()),
				principal.userId()));
		presenter.present(new PromotionCreateResponse(saved));
	}

	private MoneyDomain money(BigDecimal value){
		return value == null ? null : new MoneyDomain(value);
	}

	private Instant instant(Long epochMilli){
		return epochMilli == null ? null : Instant.ofEpochMilli(epochMilli);
	}

}
