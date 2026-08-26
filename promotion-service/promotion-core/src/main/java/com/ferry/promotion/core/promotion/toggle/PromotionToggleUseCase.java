package com.ferry.promotion.core.promotion.toggle;

import com.ferry.promotion.domain.token.PromotionAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionToggleUseCase{
	void execute(PromotionToggleRequest request, PromotionAuthPrincipal principal, PromotionTogglePresenter presenter);
}
