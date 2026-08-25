package com.ferry.order.core.invoice.link;

import com.ferry.order.core.tools.OrderValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record InvoiceLinkRequest(@NotBlank String orderId) implements OrderValidation{
}
