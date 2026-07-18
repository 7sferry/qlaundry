package com.ferry.user.gateway.session.repository;

import com.ferry.user.gateway.session.entity.UserSessionJpaEntity;
import com.ferry.user.gateway.session.entity.UserSessionTypeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface UserSessionTypeJpaRepository extends JpaRepository<UserSessionTypeJpaEntity, Integer>{
}
