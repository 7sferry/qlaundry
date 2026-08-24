package com.ferry.user.gateway.staff.repository;

import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.detail.StaffEmailDetailProjection;
import com.ferry.user.domain.staff.list.StaffEmailListProjection;
import com.ferry.user.domain.tenant.resendconfirmation.TenantAdminContactProjection;
import com.ferry.user.gateway.staff.entity.StaffEmailJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffEmailJpaRepository extends JpaRepository<StaffEmailJpaEntity, String>{
	@Query("select s " +
			"from StaffEmailJpaEntity s " +
			"where " +
			"(:#{#filter?.staffId} is null or s.staff.id = :#{#filter?.staffId}) AND " +
			"(coalesce(:#{#filter?.staffIds}, null) is null or s.staff.id IN :#{#filter?.staffIds}) " +
			"and s.deleted IS FALSE ")
	<T> List<T> findAllWithFilter(@Param("filter") StaffEmailFilter filter, Class<T> clazz);

	@Query("select new com.ferry.user.domain.staff.detail.StaffEmailDetailProjection(s.emailCipher, s.staffId) " +
			"from StaffEmailJpaEntity s " +
			"where " +
			"(:#{#filter?.staffId} is null or s.staff.id = :#{#filter?.staffId}) AND " +
			"(coalesce(:#{#filter?.staffIds}, null) is null or s.staff.id IN :#{#filter?.staffIds}) " +
			"and s.deleted IS FALSE ")
	List<StaffEmailDetailProjection> findDetailCipherRowsWithFilter(@Param("filter") StaffEmailFilter filter);

	@Query("select new com.ferry.user.domain.staff.list.StaffEmailListProjection(s.staffId, s.emailCipher) " +
			"from StaffEmailJpaEntity s " +
			"where " +
			"(:#{#filter?.staffId} is null or s.staff.id = :#{#filter?.staffId}) AND " +
			"(coalesce(:#{#filter?.staffIds}, null) is null or s.staff.id IN :#{#filter?.staffIds}) " +
			"and s.deleted IS FALSE ")
	List<StaffEmailListProjection> findListCipherRowsWithFilter(@Param("filter") StaffEmailFilter filter);

	@Query("select se " +
			"FROM StaffEmailJpaEntity se " +
			"WHERE exists(SELECT 1 FROM StaffJpaEntity s WHERE s.id = se.staffId AND s.username = :username AND s.deleted IS FALSE) " +
			"AND se.deleted IS FALSE " +
			"AND se.emailCipher IS NOT NULL " +
			"ORDER BY se.id " +
			"LIMIT 1")
	Optional<StaffEmailJpaEntity> findForForgottenPassword(@Param("username") String username);

	@Query("select new com.ferry.user.domain.tenant.resendconfirmation.TenantAdminContactProjection(se.emailCipher, s.fullName, s.username, se.staffId) " +
			"FROM StaffEmailJpaEntity se " +
			"JOIN StaffJpaEntity s ON s.id = se.staffId " +
			"WHERE s.tenantId = :tenantId AND s.roleId = 1 AND s.deleted IS FALSE " +
			"AND se.deleted IS FALSE " +
			"AND se.emailCipher IS NOT NULL " +
			"ORDER BY se.id " +
			"LIMIT 1")
	Optional<TenantAdminContactProjection> findAdminContactForTenant(@Param("tenantId") String tenantId);

	@Modifying
	@Query("update StaffEmailJpaEntity e set e.deleted = true, e.updatedBy = :updatedBy, e.updatedAt = CURRENT_TIMESTAMP " +
			"where e.staffId = :staffId and e.deleted is false")
	void softDeleteByStaffId(@Param("staffId") String staffId, @Param("updatedBy") String updatedBy);

}
