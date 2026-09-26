package com.ferry.promotion.gateway.promotion.repository;

import com.ferry.promotion.gateway.promotion.entity.PromotionRedemptionJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionRedemptionJpaRepository extends JpaRepository<PromotionRedemptionJpa, String>{

	Optional<PromotionRedemptionJpa> findByReferenceIdAndCodeAndTenantIdAndDeletedIsFalse(String referenceId,
	                                                                                            String code,
	                                                                                            String tenantId);

	List<PromotionRedemptionJpa> findAllByReferenceIdAndTenantIdAndDeletedIsFalse(String referenceId,
	                                                                                    String tenantId);

}
