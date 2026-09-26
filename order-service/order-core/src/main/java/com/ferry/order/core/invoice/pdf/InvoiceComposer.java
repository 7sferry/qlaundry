package com.ferry.order.core.invoice.pdf;

import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPromotion;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface InvoiceComposer{
	byte[] compose(Order order, List<OrderItem> items, List<OrderPromotion> promotions);
}
