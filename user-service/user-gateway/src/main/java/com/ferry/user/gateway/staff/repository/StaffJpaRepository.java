package com.ferry.user.gateway.staff.repository;

import com.ferry.user.gateway.staff.entity.StaffJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffJpaRepository extends JpaRepository<StaffJpaEntity, String>{
	boolean existsByUsername(String username);
}
