package com.ferry.promotion.core.promotion.create;

import com.ferry.promotion.domain.token.PromotionAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionCreateUseCase{
	void execute(PromotionCreateRequest request, PromotionAuthPrincipal principal, PromotionCreatePresenter presenter);
}
