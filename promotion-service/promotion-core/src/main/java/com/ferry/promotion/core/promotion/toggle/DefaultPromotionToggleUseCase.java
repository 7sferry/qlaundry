package com.ferry.promotion.core.promotion.toggle;

import com.ferry.promotion.domain.common.exception.NotFoundException;
import com.ferry.promotion.domain.common.exception.PromotionForbiddenActionException;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.staff.StaffRole;
import com.ferry.promotion.domain.tenant.TenantId;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultPromotionToggleUseCase implements PromotionToggleUseCase{
	private final PromotionToggleGateway gateway;

	@Override
	public void execute(PromotionToggleRequest request, PromotionAuthPrincipal principal,
	                    PromotionTogglePresenter presenter){
		if(principal.role() != StaffRole.SUPER_STAFF){
			throw new PromotionForbiddenActionException("Only super staff can manage promotions");
		}
		request.validate();
		PromotionId promotionId = new PromotionId(request.promotionId());
		TenantId tenantId = new TenantId(principal.tenantId());
		Promotion promotion = gateway.findById(promotionId, tenantId)
				.orElseThrow(() -> new NotFoundException("Promotion Not Found"));
		Promotion saved = gateway.save(promotion.changeActive(request.active(), principal.userId()));
		presenter.present(new PromotionToggleResponse(saved));
	}

}
