package com.ferry.order.core.analytics.backfill;

import com.ferry.order.core.analytics.OrderAnalyticsEvent;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.core.analytics.OrderAnalyticsOutboxConstant;
import com.ferry.order.core.analytics.LaundryServiceAnalyticsMessage;
import com.ferry.order.core.analytics.OrderAnalyticsMessage;
import com.ferry.order.domain.analytics.OrderAnalyticsEventType;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.service.LaundryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class DefaultOrderAnalyticsBackfillUseCase implements OrderAnalyticsBackfillUseCase{
	private final OrderAnalyticsBackfillGateway gateway;
	private final OrderAnalyticsPublisher publisher;
	private final int batchSize;

	@Override
	public OrderAnalyticsBackfillResponse execute(OrderAnalyticsBackfillRequest request){
		String tenantId = request.tenantIdOrNull();
		int services = 0;
		String afterServiceId = null;
		List<LaundryService> servicePage;
		do{
			servicePage = gateway.findServicesAfter(tenantId, afterServiceId, batchSize);
			for(LaundryService service : servicePage){
				publisher.publish(publisher.save(OrderAnalyticsEvent.laundryService(
						OrderAnalyticsEventType.LAUNDRY_SERVICE_BACKFILLED, LaundryServiceAnalyticsMessage.from(service),
						OrderAnalyticsOutboxConstant.BACKFILL_ACTOR)));
				afterServiceId = service.id();
				services++;
			}
		}while(servicePage.size() == batchSize);
		int orders = 0;
		String afterOrderId = null;
		List<Order> orderPage;
		do{
			orderPage = gateway.findOrdersAfter(tenantId, afterOrderId, batchSize);
			for(Order order : orderPage){
				OrderId orderId = new OrderId(order.id());
				publisher.publish(publisher.save(OrderAnalyticsEvent.order(OrderAnalyticsEventType.ORDER_BACKFILLED,
						OrderAnalyticsMessage.from(order, gateway.findItemsByOrderId(orderId),
								gateway.findPromotionsByOrderId(orderId)), OrderAnalyticsOutboxConstant.BACKFILL_ACTOR)));
				afterOrderId = order.id();
				orders++;
			}
			if(!orderPage.isEmpty()){
				log.info("Analytics backfill published {} order(s) so far", orders);
			}
		}while(orderPage.size() == batchSize);
		return new OrderAnalyticsBackfillResponse(orders, services);
	}

}
