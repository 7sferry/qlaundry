package com.ferry.promotion.core.promotion.detail;

import com.ferry.promotion.domain.common.exception.NotFoundException;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.tenant.TenantId;
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
		PromotionId promotionId = new PromotionId(request.promotionId());
		TenantId tenantId = new TenantId(principal.tenantId());
		Promotion promotion = gateway.findById(promotionId, tenantId)
				.orElseThrow(() -> new NotFoundException("Promotion Not Found"));
		presenter.present(new PromotionDetailResponse(promotion));
	}

}
