package com.ferry.promotion.core.promotion.preview;

import com.ferry.promotion.domain.token.PromotionAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface PromotionPreviewUseCase{
	void execute(PromotionPreviewRequest request, PromotionAuthPrincipal principal, PromotionPreviewPresenter presenter);
}
