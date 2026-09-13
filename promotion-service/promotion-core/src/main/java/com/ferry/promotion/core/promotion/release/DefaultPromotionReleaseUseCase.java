package com.ferry.promotion.core.promotion.release;

import com.ferry.promotion.domain.promotion.PromotionIdDomain;
import com.ferry.promotion.domain.promotion.PromotionRedemptionDomain;
import com.ferry.promotion.domain.tenant.TenantIdDomain;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class DefaultPromotionReleaseUseCase implements PromotionReleaseUseCase{
	private final PromotionReleaseGateway gateway;

	@Override
	public void execute(PromotionReleaseRequest request, PromotionReleasePresenter presenter){
		request.validate();
		TenantIdDomain tenantId = new TenantIdDomain(request.tenantId());
		List<PromotionRedemptionDomain> redemptions = gateway.findByReferenceId(request.referenceId(), tenantId);
		List<PromotionRedemptionDomain> released = new ArrayList<>(redemptions.size());
		for(PromotionRedemptionDomain redemption : redemptions){
			gateway.releaseUsage(new PromotionIdDomain(redemption.promotionId()), tenantId, request.releasedBy());
			released.add(gateway.save(redemption.release(request.releasedBy())));
		}
		presenter.present(new PromotionReleaseResponse(request.referenceId(), released));
	}

}
