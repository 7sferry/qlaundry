package com.ferry.analytics.webservice.promotion;

import com.ferry.analytics.core.promotion.PromotionSummaryUseCase;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class PromotionSummaryController{
	private final PromotionSummaryUseCase promotionSummaryUseCase;

	@GetMapping("/analytics/promotion")
	public ResponseEntity<?> getPromotionSummary(@AuthenticationPrincipal AnalyticsAuthPrincipal principal){
		WebPromotionSummaryPresenter presenter = new WebPromotionSummaryPresenter();
		promotionSummaryUseCase.execute(principal, presenter);
		return presenter.getResponseEntity();
	}

}
