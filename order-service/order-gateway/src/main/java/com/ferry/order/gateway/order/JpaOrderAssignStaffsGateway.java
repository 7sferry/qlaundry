package com.ferry.order.gateway.order;

import com.ferry.order.core.order.assignstaffs.OrderAssignStaffsGateway;
import com.ferry.order.domain.common.exception.OrderStaffsConflictException;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderStaff;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.gateway.order.entity.OrderJpa;
import com.ferry.order.gateway.order.entity.OrderStaffJpa;
import com.ferry.order.gateway.order.repository.OrderJpaRepository;
import com.ferry.order.gateway.order.repository.OrderStaffJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaOrderAssignStaffsGateway implements OrderAssignStaffsGateway{
	private final OrderJpaRepository orderJpaRepository;
	private final OrderStaffJpaRepository orderStaffJpaRepository;
	private final IdGenerator idGenerator;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<Order> lockById(OrderId orderId, TenantId tenantId){
		return orderJpaRepository.lockByIdAndTenantId(orderId.value(), tenantId.value())
				.map(entity -> OrderJpa.construct(entity, cryptoTool));
	}

	@Override
	public List<OrderStaff> findAllStaffsByOrderId(OrderId orderId){
		return orderStaffJpaRepository.findByOrderIdOrderById(orderId.value()).stream()
				.map(OrderStaffJpa::construct)
				.toList();
	}

	@Override
	public List<OrderStaff> saveStaffs(List<OrderStaff> staffs){
		List<OrderStaffJpa> entities = staffs.stream()
				.map(staff -> OrderStaffJpa.construct(staff.id() == null ? idGenerator.generateId() : staff.id(),
						staff, orderJpaRepository.getReferenceById(staff.orderId())))
				.toList();
		try{
			return orderStaffJpaRepository.saveAllAndFlush(entities).stream()
					.map(OrderStaffJpa::construct)
					.toList();
		}catch(DataIntegrityViolationException e){
			throw new OrderStaffsConflictException("Staff on this order were just changed by someone else. "
					+ "Reload and try again.");
		}
	}

}
