package com.ferry.promotion.core.promotion.update;

import com.ferry.promotion.domain.token.PromotionAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionUpdateUseCase{
	void execute(PromotionUpdateRequest request, PromotionAuthPrincipal principal, PromotionUpdatePresenter presenter);
}
