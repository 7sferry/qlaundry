package com.ferry.analytics.webservice.event.promotion;

import com.ferry.analytics.core.event.promotion.PromotionEventPresenter;
import com.ferry.analytics.core.event.promotion.PromotionEventResponse;
import lombok.Getter;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class StreamPromotionEventPresenter implements PromotionEventPresenter{
	private PromotionEventResponse response;

	@Override
	public void present(PromotionEventResponse response){
		this.response = response;
	}
}
