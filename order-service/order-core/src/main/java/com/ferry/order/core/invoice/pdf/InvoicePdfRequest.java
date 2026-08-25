package com.ferry.order.core.invoice.pdf;

import com.ferry.order.core.tools.OrderValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record InvoicePdfRequest(@NotBlank String token) implements OrderValidation{
}
