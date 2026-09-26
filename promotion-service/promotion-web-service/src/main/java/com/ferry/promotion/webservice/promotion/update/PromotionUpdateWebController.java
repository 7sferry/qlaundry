package com.ferry.promotion.webservice.promotion.update;

import com.ferry.promotion.core.promotion.update.PromotionUpdateRequest;
import com.ferry.promotion.core.promotion.update.PromotionUpdateUseCase;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class PromotionUpdateWebController{
	private final PromotionUpdateUseCase promotionUpdateUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PutMapping("/promotion/update")
	public ResponseEntity<?> update(@RequestBody PromotionUpdateRequest request,
	                                @AuthenticationPrincipal PromotionAuthPrincipal principal){
		PromotionUpdateWebPresenter presenter = new PromotionUpdateWebPresenter();
		promotionUpdateUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
