package com.ferry.order.gateway.order.repository;

import com.ferry.order.gateway.order.entity.OrderPromotionSagaJpaEntity;
import com.ferry.order.gateway.order.entity.OrderPromotionSagaStatusJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderPromotionSagaJpaRepository extends JpaRepository<OrderPromotionSagaJpaEntity, String>{

	@Query("select s " +
			"from OrderPromotionSagaJpaEntity s " +
			"where " +
			"s.statusId = :statusId AND " +
			"s.updatedAt < :cutoff AND " +
			"s.deleted IS FALSE " +
			"order by s.updatedAt asc, s.id asc")
	List<OrderPromotionSagaJpaEntity> findStale(@Param("statusId") short statusId, @Param("cutoff") Instant cutoff,
	                                            Pageable pageable);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update OrderPromotionSagaJpaEntity s " +
			"set s.status = :status, s.updatedBy = :updatedBy, s.updatedAt = :updatedAt " +
			"where " +
			"s.tenantId = :tenantId AND " +
			"s.referenceId = :referenceId AND " +
			"s.statusId = :fromStatusId AND " +
			"s.deleted IS FALSE")
	int transition(@Param("tenantId") String tenantId, @Param("referenceId") String referenceId,
	               @Param("fromStatusId") short fromStatusId,
	               @Param("status") OrderPromotionSagaStatusJpaEntity status,
	               @Param("updatedBy") String updatedBy, @Param("updatedAt") Instant updatedAt);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update OrderPromotionSagaJpaEntity s " +
			"set s.attempts = s.attempts + 1, s.lastError = :lastError, s.updatedBy = :updatedBy, " +
			"s.updatedAt = :updatedAt " +
			"where " +
			"s.tenantId = :tenantId AND " +
			"s.referenceId = :referenceId AND " +
			"s.statusId = :statusId AND " +
			"s.deleted IS FALSE")
	int recordFailure(@Param("tenantId") String tenantId, @Param("referenceId") String referenceId,
	                  @Param("statusId") short statusId, @Param("lastError") String lastError,
	                  @Param("updatedBy") String updatedBy, @Param("updatedAt") Instant updatedAt);

}
