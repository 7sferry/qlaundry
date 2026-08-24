package com.ferry.order.webservice.order.list;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderListWebResponse(List<Order> orders){

	public record Order(String id, String orderNumber, String customerId, String customerName, String customerPhone,
	                    String customerEmail, String customerAddress, String serviceId, String serviceName,
	                    String unit, BigDecimal unitPrice, int quantity, Double weightKg, BigDecimal subtotal,
	                    BigDecimal discount, BigDecimal totalPrice,
	                    String priority, String paymentMethod, String paymentStatus, String status,
	                    String notes, String staffNotes, long pickupAt, long estimatedDeliveryAt, Long completedAt,
	                    long createdAt, List<Item> items){

	}

	public record Item(String type, String label, int quantity){

	}

}
