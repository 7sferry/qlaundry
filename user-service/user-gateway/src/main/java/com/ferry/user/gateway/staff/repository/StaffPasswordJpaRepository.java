package com.ferry.user.gateway.staff.repository;

import com.ferry.user.domain.staff.StaffPasswordProjection;
import com.ferry.user.gateway.staff.entity.StaffPasswordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface StaffPasswordJpaRepository extends JpaRepository<StaffPasswordJpaEntity, String>{

	@Query("select new com.ferry.user.domain.staff.StaffPasswordProjection(p.password) " +
			"from StaffPasswordJpaEntity p where p.staffId = :staffId and p.deleted is false order by p.createdAt desc" +
			" limit 1")
	Optional<StaffPasswordProjection> findCurrent(@Param("staffId") String staffId);

	@Query("select new com.ferry.user.domain.staff.StaffPasswordProjection(p.password) " +
			"from StaffPasswordJpaEntity p where p.staffId = :staffId and p.updatedAt >= :since")
	List<StaffPasswordProjection> findRecent(@Param("staffId") String staffId, @Param("since") Instant since);

	@Modifying
	@Query("update StaffPasswordJpaEntity p set p.deleted = true, p.updatedBy = :updatedBy, p.updatedAt = CURRENT_TIMESTAMP " +
			"where p.staffId = :staffId and p.deleted is false")
	void softDeleteByStaffId(@Param("staffId") String staffId, @Param("updatedBy") String updatedBy);

}
