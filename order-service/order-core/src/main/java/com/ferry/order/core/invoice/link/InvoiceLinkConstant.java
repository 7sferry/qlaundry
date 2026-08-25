package com.ferry.order.core.invoice.link;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class InvoiceLinkConstant{
	public static final Duration LINK_TTL = Duration.ofMinutes(30);
	public static final String ORDER_ID_FIELD = "orderId";
	public static final String TENANT_ID_FIELD = "tenantId";
}
