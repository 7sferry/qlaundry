package com.ferry.order.gateway.order.repository;

import com.ferry.order.domain.customer.totals.CustomerOrderTotalsProjection;
import com.ferry.order.domain.order.OrderFilter;
import com.ferry.order.domain.order.schedule.OrderScheduleProjection;
import com.ferry.order.gateway.order.entity.OrderJpa;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderJpaRepository extends JpaRepository<OrderJpa, String>{

	Optional<OrderJpa> findByIdAndTenantIdAndDeletedIsFalse(String id, String tenantId);

	@Query("select o " +
			"from OrderJpa o " +
			"where " +
			"(:tenantId is null or o.tenantId = :tenantId) AND " +
			"(:afterId is null or o.id > :afterId) " +
			"order by o.id asc")
	List<OrderJpa> findBackfillPage(@Param("tenantId") String tenantId, @Param("afterId") String afterId,
	                                      Pageable pageable);

	@Query("select o " +
			"from OrderJpa o " +
			"where " +
			"(:#{#filter?.tenantId} is null or o.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.statusValue()} is null or o.statusId = :#{#filter?.statusValue()}) AND " +
			"(:#{#filter?.priorityValue()} is null or o.priorityId = :#{#filter?.priorityValue()}) AND " +
			"(:#{#filter?.customerId} is null or o.customerId = :#{#filter?.customerId}) AND " +
			"(:#{#filter?.orderNumberStartsWith()} is null or upper(o.orderNumber) like :#{#filter?.orderNumberStartsWith()}) AND " +
			"(:#{#filter?.from} is null or o.createdAt >= :#{#filter?.from}) AND " +
			"(:#{#filter?.to} is null or o.createdAt <= :#{#filter?.to}) AND " +
			"(:#{#filter?.cursor?.id} is null or o.id > :#{#filter?.cursor?.id}) AND " +
			"o.deleted IS FALSE " +
			"order by o.id asc")
	List<OrderJpa> findAfterById(@Param("filter") OrderFilter filter, Pageable pageable);

	@Query("select o " +
			"from OrderJpa o " +
			"where " +
			"(:#{#filter?.tenantId} is null or o.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.statusValue()} is null or o.statusId = :#{#filter?.statusValue()}) AND " +
			"(:#{#filter?.priorityValue()} is null or o.priorityId = :#{#filter?.priorityValue()}) AND " +
			"(:#{#filter?.customerId} is null or o.customerId = :#{#filter?.customerId}) AND " +
			"(:#{#filter?.orderNumberStartsWith()} is null or upper(o.orderNumber) like :#{#filter?.orderNumberStartsWith()}) AND " +
			"(:#{#filter?.from} is null or o.createdAt >= :#{#filter?.from}) AND " +
			"(:#{#filter?.to} is null or o.createdAt <= :#{#filter?.to}) AND " +
			"(:#{#filter?.cursor?.id} is null or o.id < :#{#filter?.cursor?.id}) AND " +
			"o.deleted IS FALSE " +
			"order by o.id desc")
	List<OrderJpa> findBeforeById(@Param("filter") OrderFilter filter, Pageable pageable);

	@Query("select o " +
			"from OrderJpa o " +
			"where " +
			"(:#{#filter?.tenantId} is null or o.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.statusValue()} is null or o.statusId = :#{#filter?.statusValue()}) AND " +
			"(:#{#filter?.priorityValue()} is null or o.priorityId = :#{#filter?.priorityValue()}) AND " +
			"(:#{#filter?.customerId} is null or o.customerId = :#{#filter?.customerId}) AND " +
			"(:#{#filter?.orderNumberStartsWith()} is null or upper(o.orderNumber) like :#{#filter?.orderNumberStartsWith()}) AND " +
			"(:#{#filter?.from} is null or o.createdAt >= :#{#filter?.from}) AND " +
			"(:#{#filter?.to} is null or o.createdAt <= :#{#filter?.to}) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or o.customerName > :#{#filter?.cursor?.sortValue} or " +
			"  (o.customerName = :#{#filter?.cursor?.sortValue} and o.id > :#{#filter?.cursor?.id})) AND " +
			"o.deleted IS FALSE " +
			"order by o.customerName asc, o.id asc")
	List<OrderJpa> findAfterByCustomerName(@Param("filter") OrderFilter filter, Pageable pageable);

	@Query("select o " +
			"from OrderJpa o " +
			"where " +
			"(:#{#filter?.tenantId} is null or o.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.statusValue()} is null or o.statusId = :#{#filter?.statusValue()}) AND " +
			"(:#{#filter?.priorityValue()} is null or o.priorityId = :#{#filter?.priorityValue()}) AND " +
			"(:#{#filter?.customerId} is null or o.customerId = :#{#filter?.customerId}) AND " +
			"(:#{#filter?.orderNumberStartsWith()} is null or upper(o.orderNumber) like :#{#filter?.orderNumberStartsWith()}) AND " +
			"(:#{#filter?.from} is null or o.createdAt >= :#{#filter?.from}) AND " +
			"(:#{#filter?.to} is null or o.createdAt <= :#{#filter?.to}) AND " +
			"(:#{#filter?.cursor?.sortValue} is null or o.customerName < :#{#filter?.cursor?.sortValue} or " +
			"  (o.customerName = :#{#filter?.cursor?.sortValue} and o.id < :#{#filter?.cursor?.id})) AND " +
			"o.deleted IS FALSE " +
			"order by o.customerName desc, o.id desc")
	List<OrderJpa> findBeforeByCustomerName(@Param("filter") OrderFilter filter, Pageable pageable);

	boolean existsByOrderNumberAndTenantId(String orderNumber, String tenantId);

	@Query("select case when count(o) > 0 then true else false end " +
			"from OrderJpa o " +
			"where o.serviceId = :serviceId and o.tenantId = :tenantId " +
			"and o.statusId not in :closedStatusIds and o.deleted is false")
	boolean hasOpenOrders(@Param("serviceId") String serviceId, @Param("tenantId") String tenantId,
	                      @Param("closedStatusIds") Collection<Short> closedStatusIds);

	@Query("select new com.ferry.order.domain.customer.totals.CustomerOrderTotalsProjection(" +
			"o.customerId, count(o), sum(o.totalPrice), max(o.createdAt)) " +
			"from OrderJpa o " +
			"where o.tenantId = :tenantId and o.customerId in :customerIds and o.deleted is false " +
			"group by o.customerId")
	List<CustomerOrderTotalsProjection> findTotalsByCustomerIds(@Param("tenantId") String tenantId,
	                                                             @Param("customerIds") Collection<String> customerIds);

	@Query("select new com.ferry.order.domain.order.schedule.OrderScheduleProjection(" +
			"o.id, o.orderNumber, o.customerName, o.pickupAt, o.statusId) " +
			"from OrderJpa o " +
			"where o.tenantId = :tenantId and o.pickupAt >= :from and o.pickupAt < :to " +
			"and o.statusId in :statusIds and o.deleted is false " +
			"order by o.pickupAt asc, o.id asc")
	List<OrderScheduleProjection> findPickupSchedule(@Param("tenantId") String tenantId, @Param("from") Instant from,
	                                                 @Param("to") Instant to,
	                                                 @Param("statusIds") Collection<Short> statusIds);

	@Query("select new com.ferry.order.domain.order.schedule.OrderScheduleProjection(" +
			"o.id, o.orderNumber, o.customerName, o.estimatedDeliveryAt, o.statusId) " +
			"from OrderJpa o " +
			"where o.tenantId = :tenantId and o.estimatedDeliveryAt >= :from and o.estimatedDeliveryAt < :to " +
			"and o.statusId in :statusIds and o.deleted is false " +
			"order by o.estimatedDeliveryAt asc, o.id asc")
	List<OrderScheduleProjection> findDeliverySchedule(@Param("tenantId") String tenantId, @Param("from") Instant from,
	                                                   @Param("to") Instant to,
	                                                   @Param("statusIds") Collection<Short> statusIds);

}
