package com.ferry.user.gateway.staff.repository;

import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.StaffPhoneFilter;
import com.ferry.user.domain.staff.list.StaffPhoneListProjection;
import com.ferry.user.gateway.staff.entity.StaffPhoneJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
			"(:#{#filter?.staffId} is null or s.staff.id = :#{#filter?.staffId}) AND " +
			"(coalesce(:#{#filter?.staffIds}, null) is null or s.staff.id IN :#{#filter?.staffIds}) AND " +
			"s.deleted IS FALSE")
	<T> List<T> findAllWithFilter(@Param("filter") StaffPhoneFilter filter, Class<T> clazz);

	@Modifying
	@Query("update StaffPhoneJpaEntity e set e.deleted = true, e.updatedBy = :updatedBy, e.updatedAt = CURRENT_TIMESTAMP " +
			"where e.staffId = :staffId and e.deleted is false")
	void softDeleteByStaffId(@Param("staffId") String staffId, @Param("updatedBy") String updatedBy);

}
