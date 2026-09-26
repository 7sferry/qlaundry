package com.ferry.promotion.gateway.promotion;

import com.ferry.promotion.core.promotion.redemption.PromotionRedemptionGateway;
import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.promotion.PromotionRedemption;
import com.ferry.promotion.domain.tenant.TenantId;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpa;
import com.ferry.promotion.gateway.promotion.entity.PromotionRedemptionJpa;
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
public class JpaPromotionRedemptionGateway implements PromotionRedemptionGateway{
	private final PromotionJpaRepository promotionJpaRepository;
	private final PromotionRedemptionJpaRepository promotionRedemptionJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public Optional<Promotion> findByCode(PromotionCode code, TenantId tenantId){
		return promotionJpaRepository.findByCodeAndTenantId(code.value(), tenantId.value())
				.map(PromotionJpa::construct);
	}

	@Override
	public Optional<Promotion> findById(PromotionId promotionId, TenantId tenantId){
		return promotionJpaRepository.findByIdAndTenantId(promotionId.value(), tenantId.value())
				.map(PromotionJpa::construct);
	}

	@Override
	public Optional<PromotionRedemption> findByReferenceId(String referenceId, String code,
	                                                              TenantId tenantId){
		return promotionRedemptionJpaRepository
				.findByReferenceIdAndCodeAndTenantIdAndDeletedIsFalse(referenceId, code, tenantId.value())
				.map(PromotionRedemptionJpa::construct);
	}

	@Override
	public boolean claimUsage(Promotion promotion){
		return promotionJpaRepository.claimUsage(promotion.id(), promotion.tenantId(), promotion.updatedBy(),
				promotion.updatedAt()) > 0;
	}

	@Override
	public PromotionRedemption save(PromotionRedemption redemption){
		String id = idGenerator.generateId();
		PromotionJpa promotion = promotionJpaRepository.getReferenceById(redemption.promotionId());
		PromotionRedemptionJpa saved = promotionRedemptionJpaRepository.save(
				PromotionRedemptionJpa.construct(id, redemption, promotion));
		return PromotionRedemptionJpa.construct(saved);
	}

	@Override
	public void rollback(){
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	}

}
