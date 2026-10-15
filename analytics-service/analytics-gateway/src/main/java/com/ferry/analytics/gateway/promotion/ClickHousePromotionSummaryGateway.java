package com.ferry.analytics.gateway.promotion;

import com.ferry.analytics.core.promotion.PromotionSummaryGateway;
import com.ferry.analytics.domain.period.MonthToDateWindow;
import com.ferry.analytics.domain.promotion.PromotionSummaryProjection;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.gateway.common.AnalyticStore;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class ClickHousePromotionSummaryGateway implements PromotionSummaryGateway{
	private static final String PROMOTION_COUNT_QUERY = """
			SELECT
			    toInt64(count()) AS total_promotions,
			    toInt64(countIf(active = 1 AND start_at <= {now:DateTime64(3, 'UTC')} AND end_at > {now:DateTime64(3, 'UTC')})) AS active_promotions,
			    toInt64(countIf(active = 1 AND end_at > {now:DateTime64(3, 'UTC')} AND end_at <= {expiringBefore:DateTime64(3, 'UTC')})) AS expiring_soon
			FROM promotions_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private static final String REDEMPTION_QUERY = """
			SELECT toInt64(count()) AS redemptions
			FROM order_promotions FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			  AND order_id IN (
			      SELECT order_id
			      FROM orders_current FINAL
			      WHERE tenant_id = {tenantId:String}
			        AND deleted = 0
			        AND status != 'CANCELLED'
			        AND created_at >= {from:DateTime64(3, 'UTC')}
			        AND created_at < {to:DateTime64(3, 'UTC')})
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private final AnalyticStore store;

	@Override
	public PromotionSummaryProjection summarize(TenantId tenantId, MonthToDateWindow window, Instant expiringBefore){
		Map<String, Object> countParams = Map.of(
				"tenantId", tenantId.value(),
				"now", store.dateTime(window.to()),
				"expiringBefore", store.dateTime(expiringBefore));
		PromotionCounts counts = store.query(PROMOTION_COUNT_QUERY, countParams,
						reader -> new PromotionCounts(reader.getLong("total_promotions"),
								reader.getLong("active_promotions"), reader.getLong("expiring_soon")))
				.stream()
				.findFirst()
				.orElse(new PromotionCounts(0L, 0L, 0L));
		Map<String, Object> redemptionParams = Map.of(
				"tenantId", tenantId.value(),
				"from", store.dateTime(window.from()),
				"to", store.dateTime(window.to()));
		long redemptions = store.query(REDEMPTION_QUERY, redemptionParams, reader -> reader.getLong("redemptions"))
				.stream()
				.findFirst()
				.orElse(0L);
		return new PromotionSummaryProjection(counts.total(), counts.active(), counts.expiringSoon(), redemptions);
	}

	private record PromotionCounts(
		long total,
		long active,
		long expiringSoon){
	}

}
