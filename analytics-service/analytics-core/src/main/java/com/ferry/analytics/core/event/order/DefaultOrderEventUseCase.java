package com.ferry.analytics.core.event.order;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;
import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.domain.event.ConsumedEventDomain;
import com.ferry.analytics.domain.event.OrderItemSnapshotDomain;
import com.ferry.analytics.domain.event.OrderPromotionSnapshotDomain;
import com.ferry.analytics.domain.event.OrderSnapshotDomain;
import lombok.RequiredArgsConstructor;

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
		OrderSnapshotDomain order = request.order();
		for(OrderItemSnapshotDomain item : request.items()){
			if(!order.orderId().equals(item.orderId()) || !order.tenantId().equals(item.tenantId())){
				throw new InvalidAnalyticStateException("Order item " + item.itemId() + " does not belong to order "
						+ order.orderId());
			}
		}
		for(OrderPromotionSnapshotDomain promotion : request.promotions()){
			if(!order.orderId().equals(promotion.orderId()) || !order.tenantId().equals(promotion.tenantId())){
				throw new InvalidAnalyticStateException("Order promotion " + promotion.promotionId()
						+ " does not belong to order " + order.orderId());
			}
		}
		gateway.upsert(order, request.items(), request.promotions());
		gateway.recordConsumed(ConsumedEventDomain.consumed(request.eventId(), AnalyticsAggregate.ORDER,
				request.type(), order.tenantId(), order.orderId(), order.version()));
		presenter.present(new OrderEventResponse(request.eventId(), order.orderId(), order.version(),
				request.items().size(), request.promotions().size()));
	}

}
