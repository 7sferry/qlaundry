package com.ferry.order.webservice.order.create;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderCreateWebResponse(String id, String orderNumber, String customerId, String customerName,
                                     String customerPhone, String customerEmail, String customerAddress,
                                     String serviceId, String serviceName, String unit, BigDecimal unitPrice,
                                     int quantity, Double weightKg, BigDecimal subtotal, BigDecimal discount,
                                     BigDecimal totalPrice,
                                     String priority, String paymentMethod, String paymentStatus, String status,
                                     String notes, long pickupAt, long estimatedDeliveryAt, long createdAt,
                                     List<Item> items){

	public record Item(String type, String label, int quantity){

	}

}
