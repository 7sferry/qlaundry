package com.ferry.promotion.webservice.promotion.preview;

import com.ferry.promotion.core.promotion.preview.PromotionPreviewRequest;
import com.ferry.promotion.core.promotion.preview.PromotionPreviewUseCase;
import com.ferry.promotion.domain.token.PromotionAuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RestController
@RequiredArgsConstructor
public class PromotionPreviewWebController{
	private final PromotionPreviewUseCase promotionPreviewUseCase;

	@PostMapping("/promotion/preview")
	public ResponseEntity<?> preview(@RequestBody PromotionPreviewRequest request,
	                                 @AuthenticationPrincipal PromotionAuthPrincipal principal){
		PromotionPreviewWebPresenter presenter = new PromotionPreviewWebPresenter();
		promotionPreviewUseCase.execute(request, principal, presenter);
		return presenter.getResponseEntity();
	}

}
