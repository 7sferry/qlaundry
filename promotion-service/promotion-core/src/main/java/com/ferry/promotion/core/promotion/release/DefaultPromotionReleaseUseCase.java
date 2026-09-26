package com.ferry.promotion.core.promotion.release;

import com.ferry.promotion.domain.promotion.PromotionId;
import com.ferry.promotion.domain.promotion.PromotionRedemption;
import com.ferry.promotion.domain.tenant.TenantId;
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
		TenantId tenantId = new TenantId(request.tenantId());
		List<PromotionRedemption> redemptions = gateway.findByReferenceId(request.referenceId(), tenantId);
		List<PromotionRedemption> released = new ArrayList<>(redemptions.size());
		for(PromotionRedemption redemption : redemptions){
			gateway.releaseUsage(new PromotionId(redemption.promotionId()), tenantId, request.releasedBy());
			released.add(gateway.save(redemption.release(request.releasedBy())));
		}
		presenter.present(new PromotionReleaseResponse(request.referenceId(), released));
	}

}
