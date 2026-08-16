package com.ferry.user.gateway.tenant.repository;

import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TenantJpaRepository extends JpaRepository<TenantJpaEntity, String>{
	<T> Optional<T> findByIdAndDeletedIsFalse(String id, Class<T> clazz);

	List<TenantJpaEntity> findAllByStatusIdAndDeletedIsFalseAndCreatedAtBefore(short statusId, Instant cutoff);

	@Modifying
	@Query("update TenantJpaEntity t set t.username = null, t.deleted = true, t.updatedAt = CURRENT_TIMESTAMP " +
			"where t.id = :tenantId and t.deleted is false")
	void markDeleted(@Param("tenantId") String tenantId);
}
