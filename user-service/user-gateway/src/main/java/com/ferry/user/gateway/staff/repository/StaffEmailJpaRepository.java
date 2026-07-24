package com.ferry.user.gateway.staff.repository;

import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.forgottenpassword.StaffEmailForgottenPasswordProjection;
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
	@Query("select s " +
			"from StaffEmailJpaEntity s " +
			"where " +
			"(:#{#filter?.staffId} is null or s.staff.id = :#{#filter?.staffId}) AND " +
			"(coalesce(:#{#filter?.staffIds}, null) is null or s.staff.id IN :#{#filter?.staffIds}) " +
			"and s.deleted IS FALSE ")
	<T> List<T> findAllWithFilter(@Param("filter") StaffEmailFilter filter, Class<T> clazz);

	@Query("select se " +
			"FROM StaffEmailJpaEntity se " +
			"WHERE exists(SELECT 1 FROM StaffJpaEntity s WHERE s.id = se.staffId AND s.username = :username AND s.deleted IS FALSE) " +
			"AND se.deleted IS FALSE " +
			"AND se.email IS NOT NULL " +
			"ORDER BY se.id " +
			"LIMIT 1")
	Optional<StaffEmailForgottenPasswordProjection> findForForgottenPassword(@Param("username") String username);

}
