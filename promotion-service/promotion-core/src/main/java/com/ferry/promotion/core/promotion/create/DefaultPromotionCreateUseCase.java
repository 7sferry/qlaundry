package com.ferry.promotion.core.promotion.create;

import com.ferry.promotion.domain.common.Money;
import com.ferry.promotion.domain.common.Note;
import com.ferry.promotion.domain.common.exception.InvalidPromotionStateException;
import com.ferry.promotion.domain.common.exception.PromotionForbiddenActionException;
import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.staff.StaffRole;
import com.ferry.promotion.domain.tenant.TenantId;
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
		TenantId tenantId = new TenantId(principal.tenantId());
		PromotionCode code = new PromotionCode(request.code());
		if(gateway.existsByCode(code, tenantId)){
			throw new InvalidPromotionStateException("Promotion code already exists");
		}
		boolean combinable = request.combinable() == null || request.combinable();
		Promotion saved = gateway.save(Promotion.create(tenantId.value(), code, request.name(),
				new Note(request.description()), request.type(), request.percentage(),
				money(request.amount()), money(request.maxDiscountAmount()), money(request.minSubtotal()),
				combinable, request.usageLimit(), instant(request.startAt()), instant(request.endAt()),
				principal.userId()));
		presenter.present(new PromotionCreateResponse(saved));
	}

	private Money money(BigDecimal value){
		return value == null ? null : new Money(value);
	}

	private Instant instant(Long epochMilli){
		return epochMilli == null ? null : Instant.ofEpochMilli(epochMilli);
	}

}
