package com.ferry.analytics.core.promotion;

import com.ferry.analytics.core.constant.AnalyticsConstant;
import com.ferry.analytics.domain.period.MonthToDateWindow;
import com.ferry.analytics.domain.promotion.PromotionSummaryProjection;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultPromotionSummaryUseCase implements PromotionSummaryUseCase{
	private final PromotionSummaryGateway gateway;
	private final Clock clock;

	@Override
	public void execute(AnalyticsAuthPrincipal principal, PromotionSummaryPresenter presenter){
		TenantId tenantId = new TenantId(principal.tenantId());
		Instant now = clock.instant();
		MonthToDateWindow window = MonthToDateWindow.endingAt(now, principal.timeZone());
		PromotionSummaryProjection summary = gateway.summarize(tenantId, window,
				now.plus(AnalyticsConstant.EXPIRING_SOON_WINDOW));
		presenter.present(new PromotionSummaryResponse(summary.totalPromotions(), summary.activePromotions(),
				summary.expiringSoon(), summary.monthRedemptions()));
	}

}
