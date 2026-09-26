package com.ferry.promotion.webservice.internal.promotion.release;

import com.ferry.promotion.client.InternalPromotionPaths;
import com.ferry.promotion.core.promotion.release.PromotionReleaseRequest;
import com.ferry.promotion.core.promotion.release.PromotionReleaseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RestController
@RequiredArgsConstructor
public class PromotionReleaseWebController{
	private final PromotionReleaseUseCase promotionReleaseUseCase;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@PostMapping(InternalPromotionPaths.RELEASE_PATH)
	public ResponseEntity<?> release(@RequestBody PromotionReleaseRequest request){
		PromotionReleaseWebPresenter presenter = new PromotionReleaseWebPresenter();
		promotionReleaseUseCase.execute(request, presenter);
		return presenter.getResponseEntity();
	}

}
