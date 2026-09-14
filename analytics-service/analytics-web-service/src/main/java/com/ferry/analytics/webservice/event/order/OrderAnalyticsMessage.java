package com.ferry.analytics.webservice.event.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderAnalyticsMessage(String tenantId, String orderId, String orderNumber, String customerId,
                                    String serviceId, String serviceName, String unit, BigDecimal unitPrice,
                                    int quantity, Double weightKg, BigDecimal subtotal, BigDecimal discount,
                                    BigDecimal totalPrice, String priority, String paymentMethod,
                                    String paymentStatus, String status, Long pickupAt, Long estimatedDeliveryAt,
                                    Long completedAt, Long createdAt, Long updatedAt, boolean deleted, int version,
                                    List<Item> items, List<Promotion> promotions){

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Item(String itemId, String type, String label, int quantity, boolean deleted, int version,
	                   Long createdAt){
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Promotion(String promotionId, String code, BigDecimal discountAmount, boolean deleted, int version,
	                        Long createdAt){
	}

}
