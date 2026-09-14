package com.ferry.analytics.gateway.event;

import com.ferry.analytics.core.event.order.OrderEventGateway;
import com.ferry.analytics.domain.event.ConsumedEventDomain;
import com.ferry.analytics.domain.event.OrderItemSnapshotDomain;
import com.ferry.analytics.domain.event.OrderPromotionSnapshotDomain;
import com.ferry.analytics.domain.event.OrderSnapshotDomain;
import com.ferry.analytics.gateway.clickhouse.ClickHouseStore;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class OrderEventClickHouseGateway implements OrderEventGateway{
	private static final String ORDERS_TABLE = "orders_current";
	private static final String ITEMS_TABLE = "order_items";
	private static final String PROMOTIONS_TABLE = "order_promotions";
	private static final String CONSUMED_EVENTS_TABLE = "consumed_events";

	private final ClickHouseStore store;

	@Override
	public void upsert(OrderSnapshotDomain order, List<OrderItemSnapshotDomain> items,
	                   List<OrderPromotionSnapshotDomain> promotions){
		store.insert(ORDERS_TABLE, List.of(construct(order)));
		store.insert(ITEMS_TABLE, items.stream().map(OrderEventClickHouseGateway::construct).toList());
		store.insert(PROMOTIONS_TABLE, promotions.stream().map(OrderEventClickHouseGateway::construct).toList());
	}

	@Override
	public void recordConsumed(ConsumedEventDomain event){
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("event_id", event.eventId());
		row.put("aggregate", event.aggregate().name());
		row.put("type", event.type());
		row.put("tenant_id", event.tenantId());
		row.put("aggregate_id", event.aggregateId());
		row.put("version", event.version());
		row.put("consumed_at", ClickHouseStore.dateTime(event.consumedAt()));
		row.put("created_at", ClickHouseStore.dateTime(event.consumedAt()));
		store.insert(CONSUMED_EVENTS_TABLE, List.of(row));
	}

	private static Map<String, Object> construct(OrderSnapshotDomain order){
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("tenant_id", order.tenantId());
		row.put("order_id", order.orderId());
		row.put("order_number", order.orderNumber());
		row.put("customer_id", order.customerId());
		row.put("service_id", order.serviceId());
		row.put("service_name", order.serviceName());
		row.put("unit", order.unit());
		row.put("unit_price", order.unitPrice());
		row.put("quantity", order.quantity());
		row.put("weight_kg", order.weightKg());
		row.put("subtotal", order.subtotal());
		row.put("discount", order.discount());
		row.put("total_price", order.totalPrice());
		row.put("priority", order.priority());
		row.put("payment_method", order.paymentMethod());
		row.put("payment_status", order.paymentStatus());
		row.put("status", order.status());
		row.put("pickup_at", ClickHouseStore.dateTime(order.pickupAt()));
		row.put("estimated_delivery_at", ClickHouseStore.dateTime(order.estimatedDeliveryAt()));
		row.put("completed_at", ClickHouseStore.dateTime(order.completedAt()));
		row.put("created_at", ClickHouseStore.dateTime(order.createdAt()));
		row.put("updated_at", ClickHouseStore.dateTime(order.updatedAt()));
		row.put("deleted", ClickHouseStore.flag(order.deleted()));
		row.put("version", order.version());
		return row;
	}

	private static Map<String, Object> construct(OrderItemSnapshotDomain item){
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("tenant_id", item.tenantId());
		row.put("order_id", item.orderId());
		row.put("item_id", item.itemId());
		row.put("type", item.type());
		row.put("label", item.label());
		row.put("quantity", item.quantity());
		row.put("deleted", ClickHouseStore.flag(item.deleted()));
		row.put("version", item.version());
		row.put("created_at", ClickHouseStore.dateTime(item.createdAt()));
		return row;
	}

	private static Map<String, Object> construct(OrderPromotionSnapshotDomain promotion){
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("tenant_id", promotion.tenantId());
		row.put("order_id", promotion.orderId());
		row.put("promotion_id", promotion.promotionId());
		row.put("code", promotion.code());
		row.put("discount_amount", promotion.discountAmount());
		row.put("deleted", ClickHouseStore.flag(promotion.deleted()));
		row.put("version", promotion.version());
		row.put("created_at", ClickHouseStore.dateTime(promotion.createdAt()));
		return row;
	}

}
