package com.ferry.order.core.order.complete;

import com.ferry.order.core.tools.OrderValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderCompleteRequest(@NotBlank String orderId, String staffNotes) implements OrderValidation{
}
