package com.ferry.user.gateway.staff.repository;

import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.StaffFilter;
import com.ferry.user.domain.staff.list.StaffEmailListProjection;
import com.ferry.user.gateway.staff.entity.StaffEmailJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffEmailJpaRepository extends JpaRepository<StaffEmailJpaEntity, String>{
	<T> List<T> findByStaffId(String staffId, Class<T> type);

	@Query("select s " +
			"from StaffEmailJpaEntity s " +
			"where " +
			"(:#{#filter?.staffId?.value} is null or s.staff.id = :#{#filter?.staffId?.value})")
	<T> List<T> findAllWithFilter(@Param("filter") StaffEmailFilter filter, Class<T> clazz);

	@Query("select new com.ferry.user.domain.staff.list.StaffEmailListProjection(s.staff.id, s.email) " +
			"from StaffEmailJpaEntity s " +
			"where s.staff.id in :staffIds and s.deleted = false")
	List<StaffEmailListProjection> findAllByStaffIds(@Param("staffIds") List<String> staffIds);

}
