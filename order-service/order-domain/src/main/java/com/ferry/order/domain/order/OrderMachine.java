package com.ferry.order.domain.order;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineType;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Builder(toBuilder = true)
public record OrderMachine(
	String id,
	String orderId,
	String machineId,
	String machineName,
	MachineType machineType,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){

	public OrderMachine{
		if(orderId == null || orderId.isBlank()){
			throw new InvalidOrderStateException("Order id must not be blank");
		}
		if(machineId == null || machineId.isBlank()){
			throw new InvalidOrderStateException("Machine id must not be blank");
		}
		if(machineName == null || machineName.isBlank()){
			throw new InvalidOrderStateException("Machine name must not be blank");
		}
		if(machineType == null){
			throw new InvalidOrderStateException("Machine type must not be null");
		}
	}

	public static OrderMachine assign(String orderId, Machine machine, String createdBy){
		Instant now = Instant.now();
		return new OrderMachine(null, orderId, machine.id(), machine.name(), machine.type(), null, false, now,
				createdBy, now, createdBy);
	}

	public OrderMachine remove(String updatedBy){
		return toBuilder().deleted(true).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}

	public OrderMachine restore(Machine machine, String updatedBy){
		return toBuilder()
				.machineName(machine.name())
				.machineType(machine.type())
				.deleted(false)
				.updatedBy(updatedBy)
				.updatedAt(Instant.now())
				.build();
	}

}
