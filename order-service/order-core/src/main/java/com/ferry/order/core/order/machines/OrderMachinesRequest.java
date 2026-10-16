package com.ferry.order.core.order.machines;

import com.ferry.order.core.tools.OrderValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record OrderMachinesRequest(
	@NotBlank String orderId,
	@NotNull Set<String> machineIds) implements OrderValidation{
}
