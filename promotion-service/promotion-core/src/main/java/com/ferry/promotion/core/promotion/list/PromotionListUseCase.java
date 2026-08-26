package com.ferry.promotion.core.promotion.list;

import com.ferry.promotion.domain.token.PromotionAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionListUseCase{
	void execute(PromotionListRequest request, PromotionAuthPrincipal principal, PromotionListPresenter presenter);
}
