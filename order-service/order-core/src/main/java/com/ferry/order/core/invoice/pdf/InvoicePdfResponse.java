package com.ferry.order.core.invoice.pdf;

import com.ferry.order.domain.order.OrderDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record InvoicePdfResponse(OrderDomain order, byte[] pdf){
}
