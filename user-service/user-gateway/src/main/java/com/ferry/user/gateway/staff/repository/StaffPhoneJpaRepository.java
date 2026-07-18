package com.ferry.user.gateway.staff.repository;

import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.StaffPhoneFilter;
import com.ferry.user.domain.staff.list.StaffPhoneListProjection;
import com.ferry.user.gateway.staff.entity.StaffPhoneJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffPhoneJpaRepository extends JpaRepository<StaffPhoneJpaEntity, String>{

	@Query("select s " +
			"from StaffPhoneJpaEntity s " +
			"where " +
			"(:#{#filter?.staffId?.value} is null or s.staff.id = :#{#filter?.staffId?.value})")
	<T> List<T> findAllWithFilter(@Param("filter") StaffPhoneFilter filter, Class<T> clazz);

	@Query("select new com.ferry.user.domain.staff.list.StaffPhoneListProjection(s.staff.id, s.phone) " +
			"from StaffPhoneJpaEntity s " +
			"where s.staff.id in :staffIds and s.deleted = false")
	List<StaffPhoneListProjection> findAllByStaffIds(@Param("staffIds") List<String> staffIds);

}
