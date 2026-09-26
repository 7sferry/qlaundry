package com.ferry.promotion.webservice.promotion.toggle;

import com.ferry.promotion.core.promotion.toggle.PromotionToggleRequest;
import com.ferry.promotion.core.promotion.toggle.PromotionToggleUseCase;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class PromotionToggleWebController{
	private final PromotionToggleUseCase promotionToggleUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PatchMapping("/promotion/toggle")
	public ResponseEntity<?> toggle(@RequestBody PromotionToggleRequest request,
	                                @AuthenticationPrincipal PromotionAuthPrincipal principal){
		PromotionToggleWebPresenter presenter = new PromotionToggleWebPresenter();
		promotionToggleUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
