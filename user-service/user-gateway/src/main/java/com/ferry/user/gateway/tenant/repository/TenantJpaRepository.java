package com.ferry.user.gateway.tenant.repository;

import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TenantJpaRepository extends JpaRepository<TenantJpaEntity, String>{
}
