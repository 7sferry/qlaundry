package com.ferry.analytics.gateway.service;

import com.ferry.analytics.core.service.ServiceSummaryGateway;
import com.ferry.analytics.domain.service.ServiceSummaryProjection;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.gateway.common.AnalyticStore;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class ClickHouseServiceSummaryGateway implements ServiceSummaryGateway{
	private static final String SUMMARY_QUERY = """
			SELECT
			    toInt64(count()) AS total_services,
			    toInt64(countIf(active = 1)) AS active_services,
			    toInt64(countIf(popular = 1)) AS popular_services,
			    toDecimal64(sumIf(price_per_unit, active = 1), 2) AS active_price_sum
			FROM laundry_services_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private final AnalyticStore store;

	@Override
	public ServiceSummaryProjection summarize(TenantId tenantId){
		return store.query(SUMMARY_QUERY, Map.of("tenantId", tenantId.value()),
						reader -> new ServiceSummaryProjection(reader.getLong("total_services"),
								reader.getLong("active_services"), reader.getLong("popular_services"),
								reader.getBigDecimal("active_price_sum")))
				.stream()
				.findFirst()
				.orElse(new ServiceSummaryProjection(0L, 0L, 0L, BigDecimal.ZERO));
	}

}
