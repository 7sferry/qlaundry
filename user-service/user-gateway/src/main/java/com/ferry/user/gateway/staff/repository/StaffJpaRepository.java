package com.ferry.user.gateway.staff.repository;

import com.ferry.user.domain.staff.StaffFilter;
import com.ferry.user.domain.staff.StaffListFilter;
import com.ferry.user.domain.staff.detail.StaffDetailProjection;
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
	<T> Optional<T> findByUsername(String username, Class<T> clazz);

	@Query("select s " +
			"from StaffJpaEntity s " +
			"where " +
			"(:#{#filter?.username?.value} is null or s.username = :#{#filter?.username?.value}) AND " +
			"(:#{#filter?.tenantId?.value} is null or s.tenant.id = :#{#filter?.tenantId?.value}) ")
	<T> Optional<T> findWithFilter(@Param("filter") StaffFilter filter, Class<T> clazz);

	@Query("select s " +
			"from StaffJpaEntity s " +
			"where " +
			"(:#{#filter?.fullNameLike()} is null or lower(s.fullName) like :#{#filter?.fullNameLike()}) AND " +
			"(:#{#filter?.tenantId?.value} is null or s.tenant.id = :#{#filter?.tenantId?.value}) AND " +
			"s.deleted = false " +
			"order by s.fullName")
	<T> List<T> findAllWithFilter(@Param("filter") StaffListFilter filter, Class<T> clazz);

}
