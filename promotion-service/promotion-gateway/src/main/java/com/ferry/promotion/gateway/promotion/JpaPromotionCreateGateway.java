package com.ferry.promotion.gateway.promotion;

import com.ferry.promotion.core.promotion.create.PromotionCreateGateway;
import com.ferry.promotion.domain.promotion.PromotionCode;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.tenant.TenantId;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpa;
import com.ferry.promotion.gateway.promotion.repository.PromotionJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaPromotionCreateGateway implements PromotionCreateGateway{
	private final PromotionJpaRepository promotionJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public boolean existsByCode(PromotionCode code, TenantId tenantId){
		return promotionJpaRepository.existsByCodeAndTenantId(code.value(), tenantId.value());
	}

	@Override
	public Promotion save(Promotion promotion){
		String id = idGenerator.generateId();
		PromotionJpa saved = promotionJpaRepository.save(PromotionJpa.construct(id, promotion));
		return PromotionJpa.construct(saved);
	}

}
