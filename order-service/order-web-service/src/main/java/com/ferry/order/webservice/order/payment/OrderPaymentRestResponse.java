package com.ferry.order.webservice.order.payment;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderPaymentRestResponse(
	String id,
	String orderNumber,
	String paymentMethod,
	String paymentStatus,
	BigDecimal totalPrice,
	long updatedAt){
}
