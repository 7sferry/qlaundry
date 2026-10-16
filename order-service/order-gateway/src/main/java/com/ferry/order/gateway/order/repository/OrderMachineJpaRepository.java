package com.ferry.order.gateway.order.repository;

import com.ferry.order.domain.machine.MachineOccupancy;
import com.ferry.order.gateway.order.entity.OrderMachineJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface OrderMachineJpaRepository extends JpaRepository<OrderMachineJpa, String>{

	List<OrderMachineJpa> findByOrderIdAndDeletedIsFalseOrderById(String orderId);

	@Query("select new com.ferry.order.domain.machine.MachineOccupancy(om.machineId, o.orderNumber) " +
			"from OrderMachineJpa om join om.order o " +
			"where om.machineId in :machineIds and om.deleted is false and o.deleted is false " +
			"and o.statusId = :statusId " +
			"order by om.machineId asc, o.orderNumber asc")
	List<MachineOccupancy> findOccupancies(@Param("machineIds") Collection<String> machineIds,
	                                       @Param("statusId") short statusId);

	@Query("select new com.ferry.order.domain.machine.MachineOccupancy(om.machineId, o.orderNumber) " +
			"from OrderMachineJpa om join om.order o " +
			"where om.machineId in :machineIds and om.deleted is false and o.deleted is false " +
			"and o.statusId = :statusId and o.id <> :excludedOrderId " +
			"order by om.machineId asc, o.orderNumber asc")
	List<MachineOccupancy> findOccupanciesExcept(@Param("machineIds") Collection<String> machineIds,
	                                             @Param("statusId") short statusId,
	                                             @Param("excludedOrderId") String excludedOrderId);

}
