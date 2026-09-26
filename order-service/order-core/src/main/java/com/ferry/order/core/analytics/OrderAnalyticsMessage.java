package com.ferry.order.core.analytics;

import com.ferry.order.domain.order.ClothingType;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.order.OrderPriority;
import com.ferry.order.domain.order.OrderPromotionDomain;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.order.PaymentMethod;
import com.ferry.order.domain.order.PaymentStatus;
import com.ferry.order.domain.service.ServiceUnit;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderAnalyticsMessage(
	String tenantId,
	String orderId,
	String orderNumber,
	String customerId,
	String serviceId,
	String serviceName,
	ServiceUnit unit,
	BigDecimal unitPrice,
	int quantity,
	Double weightKg,
	BigDecimal subtotal,
	BigDecimal discount,
	BigDecimal totalPrice,
	OrderPriority priority,
	PaymentMethod paymentMethod,
	PaymentStatus paymentStatus,
	OrderStatus status,
	Long pickupAt,
	Long estimatedDeliveryAt,
	Long completedAt,
	Long createdAt,
	Long updatedAt,
	boolean deleted,
	int version,
	List<Item> items,
	List<Promotion> promotions){

	public record Item(
		String itemId,
		ClothingType type,
		String label,
		int quantity,
		boolean deleted,
		int version,
		Long createdAt){
	}

	public record Promotion(
		String promotionId,
		String code,
		BigDecimal discountAmount,
		boolean deleted,
		int version,
		Long createdAt){
	}

	public static OrderAnalyticsMessage from(OrderDomain order, List<OrderItemDomain> items,
	                                         List<OrderPromotionDomain> promotions){
		return new OrderAnalyticsMessage(order.tenantId(), order.id(), order.orderNumberValue(), order.customerId(),
				order.serviceId(), order.serviceName(), order.unit(), order.unitPrice().value(), order.quantity(),
				order.weightKg(), order.subtotal().value(), order.discount().value(), order.totalPrice().value(),
				order.priority(), order.paymentMethod(), order.paymentStatus(), order.status(),
				epochMillis(order.pickupAt()), epochMillis(order.estimatedDeliveryAt()),
				epochMillis(order.completedAt()), epochMillis(order.createdAt()), epochMillis(order.updatedAt()),
				order.deleted(), versionOf(order.version()),
				items.stream()
						.map(item -> new Item(item.id(), item.type(), item.label(), item.quantity(), item.deleted(),
								versionOf(item.version()), epochMillis(item.createdAt())))
						.toList(),
				promotions.stream()
						.map(promotion -> new Promotion(promotion.promotionId(), promotion.code(),
								promotion.discountAmount().value(), promotion.deleted(),
								versionOf(promotion.version()), epochMillis(promotion.createdAt())))
						.toList());
	}

	private static Long epochMillis(Instant instant){
		return instant == null ? null : instant.toEpochMilli();
	}

	private static int versionOf(Integer version){
		return version == null ? 0 : version;
	}

}
