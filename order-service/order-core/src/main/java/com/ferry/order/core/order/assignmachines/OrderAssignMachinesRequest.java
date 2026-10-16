package com.ferry.order.core.order.assignmachines;

import com.ferry.order.core.tools.OrderValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record OrderAssignMachinesRequest(
	@NotBlank String orderId,
	@NotNull Set<String> machineIds) implements OrderValidation{
}
