package com.ferry.analytics.core.event.order;

import com.ferry.analytics.core.event.order.OrderEventRequest.OrderEvent;
import com.ferry.analytics.core.event.order.OrderEventResponse.AppliedOrderEvent;
import com.ferry.analytics.core.event.order.OrderEventResponse.RejectedOrderEvent;
import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.OrderItemSnapshot;
import com.ferry.analytics.domain.event.OrderPromotionSnapshot;
import com.ferry.analytics.domain.event.OrderSnapshot;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class DefaultOrderEventUseCase implements OrderEventUseCase{
	private final OrderEventGateway gateway;

	@Override
	public void execute(OrderEventRequest request, OrderEventPresenter presenter){
		request.validate();
		List<OrderEvent> accepted = new ArrayList<>();
		List<RejectedOrderEvent> rejected = new ArrayList<>();
		for(OrderEvent event : request.events()){
			rejectionOf(event).ifPresentOrElse(
					reason -> rejected.add(new RejectedOrderEvent(event.eventId(), reason)),
					() -> accepted.add(event));
		}
		if(!accepted.isEmpty()){
			gateway.upsert(
					accepted.stream().map(OrderEvent::order).toList(),
					accepted.stream().flatMap(event -> event.items().stream()).toList(),
					accepted.stream().flatMap(event -> event.promotions().stream()).toList());
			gateway.recordConsumed(accepted.stream()
					.map(event -> ConsumedEvent.consumed(event.eventId(), AnalyticsAggregate.ORDER, event.type(),
							event.order().tenantId(), event.order().orderId(), event.order().version()))
					.toList());
		}
		presenter.present(new OrderEventResponse(
				accepted.stream()
						.map(event -> new AppliedOrderEvent(event.eventId(), event.order().orderId(),
								event.order().version(), event.items().size(), event.promotions().size()))
						.toList(),
				rejected));
	}

	private Optional<String> rejectionOf(OrderEvent event){
		try{
			event.validate();
		}catch(ConstraintViolationException e){
			return Optional.of(e.getMessage());
		}
		OrderSnapshot order = event.order();
		for(OrderItemSnapshot item : event.items()){
			if(!order.orderId().equals(item.orderId()) || !order.tenantId().equals(item.tenantId())){
				return Optional.of("Order item " + item.itemId() + " does not belong to order " + order.orderId());
			}
		}
		for(OrderPromotionSnapshot promotion : event.promotions()){
			if(!order.orderId().equals(promotion.orderId()) || !order.tenantId().equals(promotion.tenantId())){
				return Optional.of("Order promotion " + promotion.promotionId() + " does not belong to order "
						+ order.orderId());
			}
		}
		return Optional.empty();
	}

}
