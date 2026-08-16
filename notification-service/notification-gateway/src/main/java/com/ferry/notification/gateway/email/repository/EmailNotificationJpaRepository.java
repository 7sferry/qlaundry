package com.ferry.notification.gateway.email.repository;

import com.ferry.notification.gateway.email.entity.EmailNotificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface EmailNotificationJpaRepository extends JpaRepository<EmailNotificationJpaEntity, String>{
	Optional<EmailNotificationJpaEntity> findByReferenceId(String referenceId);
}
