package com.ferry.user.gateway.staff.repository;

import com.ferry.user.domain.staff.StaffAddressFilter;
import com.ferry.user.domain.staff.StaffPhoneFilter;
import com.ferry.user.domain.staff.list.StaffAddressListProjection;
import com.ferry.user.domain.staff.detail.StaffAddressDetailProjection;
import com.ferry.user.domain.staff.detail.StaffPhoneDetailProjection;
import com.ferry.user.gateway.staff.entity.StaffAddressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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
			"(:#{#filter?.staffId?.value} is null or s.staff.id = :#{#filter?.staffId?.value})")
	<T> List<T> findAllWithFilter(StaffAddressFilter filter, Class<T> staffPhoneDetailProjectionClass);

	@Query("select new com.ferry.user.domain.staff.list.StaffAddressListProjection(s.staff.id, s.addressLine) " +
			"from StaffAddressJpaEntity s " +
			"where s.staff.id in :staffIds and s.deleted = false")
	List<StaffAddressListProjection> findAllByStaffIds(@Param("staffIds") List<String> staffIds);
}
