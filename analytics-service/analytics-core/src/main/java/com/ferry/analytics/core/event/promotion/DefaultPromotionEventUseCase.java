package com.ferry.analytics.core.event.promotion;

import com.ferry.analytics.core.event.promotion.PromotionEventRequest.PromotionEvent;
import com.ferry.analytics.core.event.promotion.PromotionEventResponse.AppliedPromotionEvent;
import com.ferry.analytics.core.event.promotion.PromotionEventResponse.RejectedPromotionEvent;
import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.domain.event.ConsumedEvent;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultPromotionEventUseCase implements PromotionEventUseCase{
	private final PromotionEventGateway gateway;

	@Override
	public void execute(PromotionEventRequest request, PromotionEventPresenter presenter){
		request.validate();
		List<PromotionEvent> accepted = new ArrayList<>();
		List<RejectedPromotionEvent> rejected = new ArrayList<>();
		for(PromotionEvent event : request.events()){
			rejectionOf(event).ifPresentOrElse(
					reason -> rejected.add(new RejectedPromotionEvent(event.eventId(), reason)),
					() -> accepted.add(event));
		}
		if(!accepted.isEmpty()){
			gateway.upsert(accepted.stream().map(PromotionEvent::promotion).toList());
			gateway.recordConsumed(accepted.stream()
					.map(event -> ConsumedEvent.consumed(event.eventId(), AnalyticsAggregate.PROMOTION, event.type(),
							event.promotion().tenantId(), event.promotion().promotionId(),
							event.promotion().version()))
					.toList());
		}
		presenter.present(new PromotionEventResponse(
				accepted.stream()
						.map(event -> new AppliedPromotionEvent(event.eventId(), event.promotion().promotionId(),
								event.promotion().version()))
						.toList(),
				rejected));
	}

	private Optional<String> rejectionOf(PromotionEvent event){
		try{
			event.validate();
			return Optional.empty();
		}catch(ConstraintViolationException e){
			return Optional.of(e.getMessage());
		}
	}

}
