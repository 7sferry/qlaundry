package com.ferry.promotion.webservice.promotion.list;

import com.ferry.promotion.core.promotion.list.PromotionListRequest;
import com.ferry.promotion.core.promotion.list.PromotionListUseCase;
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
public class PromotionListWebController{
	private final PromotionListUseCase promotionListUseCase;

	@Transactional(readOnly = true)
	@GetMapping("/promotion/list")
	public ResponseEntity<?> getList(PromotionListRequest request,
	                                 @AuthenticationPrincipal PromotionAuthPrincipal principal){
		PromotionListWebPresenter presenter = new PromotionListWebPresenter();
		promotionListUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
