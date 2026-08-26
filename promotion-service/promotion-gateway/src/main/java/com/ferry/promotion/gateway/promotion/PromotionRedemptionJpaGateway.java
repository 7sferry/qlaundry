package com.ferry.promotion.gateway.promotion;

import com.ferry.promotion.core.promotion.redemption.PromotionRedemptionGateway;
import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.promotion.PromotionRedemptionDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpaEntity;
import com.ferry.promotion.gateway.promotion.entity.PromotionRedemptionJpaEntity;
import com.ferry.promotion.gateway.promotion.repository.PromotionJpaRepository;
import com.ferry.promotion.gateway.promotion.repository.PromotionRedemptionJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.Instant;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class PromotionRedemptionJpaGateway implements PromotionRedemptionGateway{
	private final PromotionJpaRepository promotionJpaRepository;
	private final PromotionRedemptionJpaRepository promotionRedemptionJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public Optional<PromotionDomain> findByCode(PromotionCodeDomain code, TenantIdDomain tenantId){
		return promotionJpaRepository.findByCodeAndTenantId(code.value(), tenantId.value())
				.map(PromotionJpaEntity::construct);
	}

	@Override
	public Optional<PromotionDomain> findById(PromotionIdDomain promotionId, TenantIdDomain tenantId){
		return promotionJpaRepository.findByIdAndTenantId(promotionId.value(), tenantId.value())
				.map(PromotionJpaEntity::construct);
	}

	@Override
	public Optional<PromotionRedemptionDomain> findByReferenceId(String referenceId, String code,
	                                                              TenantIdDomain tenantId){
		return promotionRedemptionJpaRepository
				.findByReferenceIdAndCodeAndTenantIdAndDeletedIsFalse(referenceId, code, tenantId.value())
				.map(PromotionRedemptionJpaEntity::construct);
	}

	@Override
	public boolean claimUsage(PromotionDomain promotion){
		return promotionJpaRepository.claimUsage(promotion.id(), promotion.tenantId(), promotion.updatedBy(),
				promotion.updatedAt()) > 0;
	}

	@Override
	public PromotionRedemptionDomain save(PromotionRedemptionDomain redemption){
		String id = idGenerator.generateId();
		PromotionJpaEntity promotion = promotionJpaRepository.getReferenceById(redemption.promotionId());
		PromotionRedemptionJpaEntity saved = promotionRedemptionJpaRepository.save(
				PromotionRedemptionJpaEntity.construct(id, redemption, promotion));
		return PromotionRedemptionJpaEntity.construct(saved);
	}

	@Override
	public void rollback(){
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	}

}
