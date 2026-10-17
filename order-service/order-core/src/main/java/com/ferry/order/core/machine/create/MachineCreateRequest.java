package com.ferry.order.core.machine.create;

import com.ferry.order.core.tools.OrderValidation;
import com.ferry.order.domain.machine.MachineType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record MachineCreateRequest(@NotBlank String name, @NotNull MachineType type,
                                   String description) implements OrderValidation{
}
