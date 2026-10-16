package com.ferry.order.gateway.order;

import com.ferry.order.core.order.process.OrderProcessGateway;
import com.ferry.order.domain.common.exception.OrderMachinesConflictException;
import com.ferry.order.domain.machine.Machine;
import com.ferry.order.domain.machine.MachineId;
import com.ferry.order.domain.machine.MachineOccupancy;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderMachine;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.order.OrderStatus;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.gateway.machine.entity.MachineJpa;
import com.ferry.order.gateway.machine.repository.MachineJpaRepository;
import com.ferry.order.gateway.order.entity.OrderItemJpa;
import com.ferry.order.gateway.order.entity.OrderJpa;
import com.ferry.order.gateway.order.entity.OrderMachineJpa;
import com.ferry.order.gateway.order.entity.OrderPromotionJpa;
import com.ferry.order.gateway.order.repository.OrderItemJpaRepository;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.order.repository.OrderMachineJpaRepository;
import com.ferry.order.gateway.order.repository.OrderPromotionJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaOrderProcessGateway implements OrderProcessGateway{
	private final OrderJpaRepository orderJpaRepository;
	private final OrderItemJpaRepository orderItemJpaRepository;
	private final OrderPromotionJpaRepository orderPromotionJpaRepository;
	private final OrderMachineJpaRepository orderMachineJpaRepository;
	private final MachineJpaRepository machineJpaRepository;
	private final IdGenerator idGenerator;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<Order> lockById(OrderId orderId, TenantId tenantId){
		return orderJpaRepository.lockByIdAndTenantId(orderId.value(), tenantId.value())
				.map(entity -> OrderJpa.construct(entity, cryptoTool));
	}

	@Override
	public Order save(Order order){
		OrderJpa saved = orderJpaRepository.saveAndFlush(OrderJpa.construct(order.id(), order, cryptoTool));
		return OrderJpa.construct(saved, cryptoTool);
	}

	@Override
	public List<OrderItem> findItemsByOrderId(OrderId orderId){
		return orderItemJpaRepository.findByOrderIdAndDeletedIsFalseOrderById(orderId.value()).stream()
				.map(OrderItemJpa::construct)
				.toList();
	}

	@Override
	public List<OrderPromotion> findPromotionsByOrderId(OrderId orderId){
		return orderPromotionJpaRepository.findByOrderIdAndDeletedIsFalseOrderById(orderId.value()).stream()
				.map(OrderPromotionJpa::construct)
				.toList();
	}

	@Override
	public List<Machine> lockMachines(Set<MachineId> machineIds, TenantId tenantId){
		List<String> ids = machineIds.stream().map(MachineId::value).toList();
		return machineJpaRepository.lockByIdsAndTenantId(ids, tenantId.value()).stream()
				.map(MachineJpa::construct)
				.toList();
	}

	@Override
	public List<MachineOccupancy> findOccupancies(Set<MachineId> machineIds, OrderId excludedOrderId){
		List<String> ids = machineIds.stream().map(MachineId::value).toList();
		return orderMachineJpaRepository.findOccupanciesExcept(ids, OrderStatus.IN_PROGRESS.getValue(),
				excludedOrderId.value());
	}

	@Override
	public List<OrderMachine> saveMachines(List<OrderMachine> machines){
		List<OrderMachineJpa> entities = machines.stream()
				.map(machine -> OrderMachineJpa.construct(idGenerator.generateId(), machine,
						orderJpaRepository.getReferenceById(machine.orderId()),
						machineJpaRepository.getReferenceById(machine.machineId())))
				.toList();
		try{
			return orderMachineJpaRepository.saveAllAndFlush(entities).stream()
					.map(OrderMachineJpa::construct)
					.toList();
		}catch(DataIntegrityViolationException e){
			throw new OrderMachinesConflictException("Machines on this order were just changed by someone else. "
					+ "Reload and try again.");
		}
	}

}
