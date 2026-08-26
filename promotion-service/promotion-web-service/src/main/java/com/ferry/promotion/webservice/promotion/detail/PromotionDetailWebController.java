package com.ferry.promotion.webservice.promotion.detail;

import com.ferry.promotion.core.promotion.detail.PromotionDetailRequest;
import com.ferry.promotion.core.promotion.detail.PromotionDetailUseCase;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class PromotionDetailWebController{
	private final PromotionDetailUseCase promotionDetailUseCase;

	@Transactional(readOnly = true)
	@GetMapping("/promotion/detail")
	public ResponseEntity<?> getDetail(PromotionDetailRequest request,
	                                   @AuthenticationPrincipal PromotionAuthPrincipal principal){
		PromotionDetailWebPresenter presenter = new PromotionDetailWebPresenter();
		promotionDetailUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
