package com.ferry.promotion.webservice.promotion.create;

import com.ferry.promotion.core.promotion.create.PromotionCreateRequest;
import com.ferry.promotion.core.promotion.create.PromotionCreateUseCase;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
public class PromotionCreateController{
	private final PromotionCreateUseCase promotionCreateUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PostMapping("/promotion/create")
	public ResponseEntity<?> create(@RequestBody PromotionCreateRequest request,
	                                @AuthenticationPrincipal PromotionAuthPrincipal principal){
		WebPromotionCreatePresenter presenter = new WebPromotionCreatePresenter();
		promotionCreateUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
