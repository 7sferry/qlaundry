package com.ferry.user.gateway.session.repository;

import com.ferry.user.gateway.session.entity.UserSessionJpa;
import com.ferry.user.gateway.session.entity.UserSessionTypeJpa;
import org.springframework.data.jpa.repository.JpaRepository;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface UserSessionTypeJpaRepository extends JpaRepository<UserSessionTypeJpa, Short>{
}
