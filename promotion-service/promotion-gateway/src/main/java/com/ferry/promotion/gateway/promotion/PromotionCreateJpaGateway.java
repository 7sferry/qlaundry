package com.ferry.promotion.gateway.promotion;

import com.ferry.promotion.core.promotion.create.PromotionCreateGateway;
import com.ferry.promotion.domain.promotion.PromotionCodeDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import com.ferry.promotion.gateway.promotion.entity.PromotionJpaEntity;
import com.ferry.promotion.gateway.promotion.repository.PromotionJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class PromotionCreateJpaGateway implements PromotionCreateGateway{
	private final PromotionJpaRepository promotionJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public boolean existsByCode(PromotionCodeDomain code, TenantIdDomain tenantId){
		return promotionJpaRepository.existsByCodeAndTenantId(code.value(), tenantId.value());
	}

	@Override
	public PromotionDomain save(PromotionDomain promotion){
		String id = idGenerator.generateId();
		PromotionJpaEntity saved = promotionJpaRepository.save(PromotionJpaEntity.construct(id, promotion));
		return PromotionJpaEntity.construct(saved);
	}

}
