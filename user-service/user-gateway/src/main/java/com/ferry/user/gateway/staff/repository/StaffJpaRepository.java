package com.ferry.user.gateway.staff.repository;

import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.gateway.staff.entity.StaffJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffJpaRepository extends JpaRepository<StaffJpaEntity, String>{
	boolean existsByUsername(String username);
	<T> Optional<T> findByUsername(String username, Class<T> clazz);
}
