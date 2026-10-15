package com.ferry.analytics.core.promotion;

import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface PromotionSummaryUseCase{
	void execute(AnalyticsAuthPrincipal principal, PromotionSummaryPresenter presenter);
}
