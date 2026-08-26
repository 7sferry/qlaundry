package com.ferry.promotion.core.promotion.toggle;

import com.ferry.promotion.domain.common.exception.NotFoundException;
import com.ferry.promotion.domain.common.exception.PromotionForbiddenActionException;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.staff.StaffRole;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
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
		PromotionIdDomain promotionId = new PromotionIdDomain(request.promotionId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		PromotionDomain promotion = gateway.findById(promotionId, tenantId)
				.orElseThrow(() -> new NotFoundException("Promotion Not Found"));
		PromotionDomain saved = gateway.save(promotion.changeActive(request.active(), principal.userId()));
		presenter.present(new PromotionToggleResponse(saved));
	}

}
