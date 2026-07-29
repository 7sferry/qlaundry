package com.ferry.user.gateway.staff.repository;

import com.ferry.user.domain.staff.StaffAddressFilter;
import com.ferry.user.domain.staff.list.StaffAddressListProjection;
import com.ferry.user.gateway.staff.entity.StaffAddressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffAddressJpaRepository extends JpaRepository<StaffAddressJpaEntity, String>{

	@Query("select s " +
			"from StaffAddressJpaEntity s " +
			"where " +
			"(:#{#filter?.staffId} is null or s.staff.id = :#{#filter?.staffId}) AND " +
			"(coalesce(:#{#filter?.staffIds}, null) is null or s.staff.id IN :#{#filter?.staffIds}) AND " +
			"s.deleted IS FALSE")
	<T> List<T> findAllWithFilter(@Param("filter") StaffAddressFilter filter, Class<T> tClass);

	@Modifying
	@Query("update StaffAddressJpaEntity e set e.deleted = true, e.updatedBy = :updatedBy, e.updatedAt = CURRENT_TIMESTAMP " +
			"where e.staffId = :staffId and e.deleted is false")
	void softDeleteByStaffId(@Param("staffId") String staffId, @Param("updatedBy") String updatedBy);

}
