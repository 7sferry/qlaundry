package com.ferry.order.gateway.analytics.repository;

import com.ferry.order.gateway.analytics.entity.AnalyticsEventJpa;
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

public interface AnalyticsEventJpaRepository extends JpaRepository<AnalyticsEventJpa, String>{

	@Query("select e " +
			"from AnalyticsEventJpa e " +
			"where " +
			"e.statusId = :statusId AND " +
			"e.createdAt < :cutoff AND " +
			"e.deleted IS FALSE " +
			"order by e.createdAt asc, e.id asc")
	List<AnalyticsEventJpa> findStale(@Param("statusId") short statusId, @Param("cutoff") Instant cutoff,
	                                        Pageable pageable);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update AnalyticsEventJpa e " +
			"set e.statusId = :statusId, e.updatedBy = :updatedBy, e.updatedAt = :updatedAt " +
			"where " +
			"e.id = :id AND " +
			"e.statusId = :fromStatusId AND " +
			"e.deleted IS FALSE")
	int transition(@Param("id") String id, @Param("fromStatusId") short fromStatusId,
	               @Param("statusId") short statusId, @Param("updatedBy") String updatedBy,
	               @Param("updatedAt") Instant updatedAt);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update AnalyticsEventJpa e " +
			"set e.attempts = e.attempts + 1, e.lastError = :lastError, e.updatedBy = :updatedBy, " +
			"e.updatedAt = :updatedAt " +
			"where " +
			"e.id = :id AND " +
			"e.statusId = :statusId AND " +
			"e.deleted IS FALSE")
	int recordFailure(@Param("id") String id, @Param("statusId") short statusId, @Param("lastError") String lastError,
	                  @Param("updatedBy") String updatedBy, @Param("updatedAt") Instant updatedAt);

}
