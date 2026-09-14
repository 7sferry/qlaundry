package com.ferry.order.core.analytics.backfill;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.AnalyticsEventPublisher;
import com.ferry.order.core.analytics.AnalyticsOutboxConstant;
import com.ferry.order.core.analytics.LaundryServiceAnalyticsMessage;
import com.ferry.order.core.analytics.OrderAnalyticsMessage;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.service.LaundryServiceDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class DefaultAnalyticsBackfillUseCase implements AnalyticsBackfillUseCase{
	private final AnalyticsBackfillGateway gateway;
	private final AnalyticsEventPublisher publisher;

	@Override
	public AnalyticsBackfillResponse execute(AnalyticsBackfillRequest request){
		String tenantId = request.tenantIdOrNull();
		int services = 0;
		String afterServiceId = null;
		List<LaundryServiceDomain> servicePage;
		do{
			servicePage = gateway.findServicesAfter(tenantId, afterServiceId,
					AnalyticsOutboxConstant.BACKFILL_BATCH_SIZE);
			for(LaundryServiceDomain service : servicePage){
				publisher.publish(publisher.save(AnalyticsEventConfig.laundryService(
						AnalyticsEventType.LAUNDRY_SERVICE_BACKFILLED, LaundryServiceAnalyticsMessage.from(service),
						AnalyticsOutboxConstant.BACKFILL_ACTOR)));
				afterServiceId = service.id();
				services++;
			}
		}while(servicePage.size() == AnalyticsOutboxConstant.BACKFILL_BATCH_SIZE);
		int orders = 0;
		String afterOrderId = null;
		List<OrderDomain> orderPage;
		do{
			orderPage = gateway.findOrdersAfter(tenantId, afterOrderId, AnalyticsOutboxConstant.BACKFILL_BATCH_SIZE);
			for(OrderDomain order : orderPage){
				OrderIdDomain orderId = new OrderIdDomain(order.id());
				publisher.publish(publisher.save(AnalyticsEventConfig.order(AnalyticsEventType.ORDER_BACKFILLED,
						OrderAnalyticsMessage.from(order, gateway.findItemsByOrderId(orderId),
								gateway.findPromotionsByOrderId(orderId)), AnalyticsOutboxConstant.BACKFILL_ACTOR)));
				afterOrderId = order.id();
				orders++;
			}
			if(!orderPage.isEmpty()){
				log.info("Analytics backfill published {} order(s) so far", orders);
			}
		}while(orderPage.size() == AnalyticsOutboxConstant.BACKFILL_BATCH_SIZE);
		return new AnalyticsBackfillResponse(orders, services);
	}

}
