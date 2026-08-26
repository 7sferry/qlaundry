package com.ferry.order.core.invoice.pdf;

import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.order.OrderPromotionDomain;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface InvoiceHtmlComposer{
	byte[] compose(OrderDomain order, List<OrderItemDomain> items, List<OrderPromotionDomain> promotions);
}
