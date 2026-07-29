package com.ferry.user.gateway.staff.repository;

import com.ferry.user.domain.staff.StaffFilter;
import com.ferry.user.gateway.staff.entity.StaffJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffJpaRepository extends JpaRepository<StaffJpaEntity, String>{
	boolean existsByUsername(String username);

	@Query("select s " +
			"FROM StaffJpaEntity s " +
			"WHERE exists(SELECT 1 FROM TenantJpaEntity t WHERE t.id = s.tenantId AND t.deleted IS FALSE) " +
			"AND s.deleted IS FALSE " +
			"AND s.username = :username ")
	<T> Optional<T> fetchByUsername(@Param("username") String username, Class<T> clazz);
	<T> Optional<T> findById(String id, Class<T> clazz);
	<T> Optional<T> findByIdAndDeletedIsFalse(String id, Class<T> clazz);
	<T> Optional<T> findByUsernameAndTenantIdAndDeletedIsFalse(String username, String tenantId, Class<T> clazz);

	@Query("select s " +
			"from StaffJpaEntity s " +
			"where " +
			"(:#{#filter?.fullNameStartsWith()} is null or lower(s.fullName) like :#{#filter?.fullNameStartsWith()}) AND " +
			"(:#{#filter?.tenantId} is null or s.tenant.id = :#{#filter?.tenantId}) AND " +
			"s.deleted IS FALSE " +
			"order by s.fullName")
	<T> List<T> findAllWithFilter(@Param("filter") StaffFilter filter, Class<T> clazz);

}
