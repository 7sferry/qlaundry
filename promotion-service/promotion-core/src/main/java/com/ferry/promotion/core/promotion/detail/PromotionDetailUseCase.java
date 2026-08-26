package com.ferry.promotion.core.promotion.detail;

import com.ferry.promotion.domain.token.PromotionAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionDetailUseCase{
	void execute(PromotionDetailRequest request, PromotionAuthPrincipal principal, PromotionDetailPresenter presenter);
}
