package com.ferry.order.core.invoice.pdf;

import com.ferry.order.domain.order.Order;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record InvoicePdfResponse(
	Order order,
	byte[] pdf){
}
