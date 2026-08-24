package com.ferry.order.gateway.order.repository;

import com.ferry.order.domain.order.OrderFilter;
import com.ferry.order.gateway.order.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, String>{

	Optional<OrderJpaEntity> findByIdAndTenantIdAndDeletedIsFalse(String id, String tenantId);

	@Query("select o " +
			"from OrderJpaEntity o " +
			"where " +
			"(:#{#filter?.tenantId} is null or o.tenantId = :#{#filter?.tenantId}) AND " +
			"(:#{#filter?.statusValue()} is null or o.statusId = :#{#filter?.statusValue()}) AND " +
			"(:#{#filter?.priorityValue()} is null or o.priorityId = :#{#filter?.priorityValue()}) AND " +
			"(:#{#filter?.customerId} is null or o.customerId = :#{#filter?.customerId}) AND " +
			"(:#{#filter?.orderNumberStartsWith()} is null or upper(o.orderNumber) like :#{#filter?.orderNumberStartsWith()}) AND " +
			"(:#{#filter?.from} is null or o.createdAt >= :#{#filter?.from}) AND " +
			"(:#{#filter?.to} is null or o.createdAt <= :#{#filter?.to}) AND " +
			"o.deleted IS FALSE " +
			"order by o.createdAt desc")
	List<OrderJpaEntity> findAllWithFilter(@Param("filter") OrderFilter filter);

	@Query("select case when count(o) > 0 then true else false end " +
			"from OrderJpaEntity o " +
			"where o.serviceId = :serviceId and o.tenantId = :tenantId " +
			"and o.statusId not in :closedStatusIds and o.deleted is false")
	boolean hasOpenOrders(@Param("serviceId") String serviceId, @Param("tenantId") String tenantId,
	                      @Param("closedStatusIds") Collection<Short> closedStatusIds);

}
