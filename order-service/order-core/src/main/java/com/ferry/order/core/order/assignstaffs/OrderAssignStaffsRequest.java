package com.ferry.order.core.order.assignstaffs;

import com.ferry.order.core.tools.OrderValidation;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record OrderAssignStaffsRequest(
	@NotBlank String orderId,
	Set<String> staffIds) implements OrderValidation{
}
