package com.ferry.order.core.machine.update;

import com.ferry.order.core.tools.OrderValidation;
import com.ferry.order.domain.machine.MachineType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record MachineUpdateRequest(@NotBlank String machineId, @NotBlank String name, @NotNull MachineType type,
                                   @Positive Double capacityKg, String description,
                                   Boolean active) implements OrderValidation{
}
