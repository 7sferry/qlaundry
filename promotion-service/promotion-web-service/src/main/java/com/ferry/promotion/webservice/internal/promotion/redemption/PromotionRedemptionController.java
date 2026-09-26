package com.ferry.promotion.webservice.internal.promotion.redemption;

import com.ferry.promotion.client.InternalPromotionPaths;
import com.ferry.promotion.core.promotion.redemption.PromotionRedemptionRequest;
import com.ferry.promotion.core.promotion.redemption.PromotionRedemptionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class PromotionRedemptionController{
	private final PromotionRedemptionUseCase promotionRedemptionUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PostMapping(InternalPromotionPaths.REDEMPTION_PATH)
	public ResponseEntity<?> redeem(@RequestBody PromotionRedemptionRequest request){
		WebPromotionRedemptionPresenter presenter = new WebPromotionRedemptionPresenter();
		promotionRedemptionUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
