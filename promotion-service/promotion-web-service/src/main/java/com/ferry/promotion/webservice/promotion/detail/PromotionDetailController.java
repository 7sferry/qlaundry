package com.ferry.promotion.webservice.promotion.detail;

import com.ferry.promotion.core.promotion.detail.PromotionDetailRequest;
import com.ferry.promotion.core.promotion.detail.PromotionDetailUseCase;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RestController
@RequiredArgsConstructor
public class PromotionDetailController{
	private final PromotionDetailUseCase promotionDetailUseCase;

	@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
	@GetMapping("/promotion/detail")
	public ResponseEntity<?> getDetail(PromotionDetailRequest request,
	                                   @AuthenticationPrincipal PromotionAuthPrincipal principal){
		WebPromotionDetailPresenter presenter = new WebPromotionDetailPresenter();
		promotionDetailUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
