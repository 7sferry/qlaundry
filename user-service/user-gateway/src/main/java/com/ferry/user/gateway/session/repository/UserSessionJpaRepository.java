package com.ferry.user.gateway.session.repository;

import com.ferry.user.gateway.session.entity.UserSessionJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface UserSessionJpaRepository extends JpaRepository<UserSessionJpa, String>{
}
