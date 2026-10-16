package com.ferry.promotion.core.analytics.backfill;

import com.ferry.promotion.core.analytics.PromotionAnalyticsEvent;
import com.ferry.promotion.core.analytics.PromotionAnalyticsOutboxConstant;
import com.ferry.promotion.core.analytics.PromotionAnalyticsMessage;
import com.ferry.promotion.core.analytics.PromotionAnalyticsPublisher;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEventType;
import com.ferry.promotion.domain.promotion.Promotion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class DefaultPromotionAnalyticsBackfillUseCase implements PromotionAnalyticsBackfillUseCase{
	private final PromotionAnalyticsBackfillGateway gateway;
	private final PromotionAnalyticsPublisher publisher;
	private final int batchSize;

	@Override
	public PromotionAnalyticsBackfillResponse execute(PromotionAnalyticsBackfillRequest request){
		String tenantId = request.tenantIdOrNull();
		int promotions = 0;
		String afterId = null;
		List<Promotion> page;
		do{
			page = gateway.findPromotionsAfter(tenantId, afterId, batchSize);
			for(Promotion promotion : page){
				publisher.publish(publisher.save(PromotionAnalyticsEvent.promotion(
						PromotionAnalyticsEventType.PROMOTION_BACKFILLED, PromotionAnalyticsMessage.from(promotion),
						PromotionAnalyticsOutboxConstant.BACKFILL_ACTOR)));
				afterId = promotion.id();
				promotions++;
			}
			if(!page.isEmpty()){
				log.info("Analytics backfill published {} promotion(s) so far", promotions);
			}
		}while(page.size() == batchSize);
		return new PromotionAnalyticsBackfillResponse(promotions);
	}

}
