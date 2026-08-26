package com.ferry.promotion.gateway.promotion.repository;

import com.ferry.promotion.gateway.promotion.entity.PromotionRedemptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionRedemptionJpaRepository extends JpaRepository<PromotionRedemptionJpaEntity, String>{

	Optional<PromotionRedemptionJpaEntity> findByReferenceIdAndCodeAndTenantIdAndDeletedIsFalse(String referenceId,
	                                                                                            String code,
	                                                                                            String tenantId);

}
