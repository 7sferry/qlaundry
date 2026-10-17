package com.ferry.user.gateway.session.repository;

import com.ferry.user.gateway.session.entity.UserSessionJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface UserSessionJpaRepository extends JpaRepository<UserSessionJpa, String>{

	@Query("select s.id from UserSessionJpa s where s.userId = :userId and s.expirationTime > :now")
	List<String> findActiveIdsByUserId(@Param("userId") String userId, @Param("now") Instant now);

	@Modifying
	@Query("update versioned UserSessionJpa s set s.expirationTime = :now, s.updatedAt = :now " +
			"where s.userId = :userId and s.expirationTime > :now")
	void expireActiveByUserId(@Param("userId") String userId, @Param("now") Instant now);
}
