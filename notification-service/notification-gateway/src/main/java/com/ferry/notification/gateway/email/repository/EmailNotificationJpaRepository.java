package com.ferry.notification.gateway.email.repository;

import com.ferry.notification.gateway.email.entity.EmailNotificationJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface EmailNotificationJpaRepository extends JpaRepository<EmailNotificationJpa, String>{
	Optional<EmailNotificationJpa> findByReferenceId(String referenceId);
}
