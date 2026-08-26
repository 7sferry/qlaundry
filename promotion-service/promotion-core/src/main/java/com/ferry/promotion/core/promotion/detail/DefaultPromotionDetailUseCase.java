package com.ferry.promotion.core.promotion.detail;

import com.ferry.promotion.domain.common.exception.NotFoundException;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultPromotionDetailUseCase implements PromotionDetailUseCase{
	private final PromotionDetailGateway gateway;

	@Override
	public void execute(PromotionDetailRequest request, PromotionAuthPrincipal principal,
	                    PromotionDetailPresenter presenter){
		request.validate();
		PromotionIdDomain promotionId = new PromotionIdDomain(request.promotionId());
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		PromotionDomain promotion = gateway.findById(promotionId, tenantId)
				.orElseThrow(() -> new NotFoundException("Promotion Not Found"));
		presenter.present(new PromotionDetailResponse(promotion));
	}

}
