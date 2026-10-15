package com.ferry.analytics.gateway.customer;

import com.ferry.analytics.core.customer.totals.CustomerTotalsGateway;
import com.ferry.analytics.domain.customer.CustomerTotalsProjection;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.gateway.common.AnalyticStore;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class ClickHouseCustomerTotalsGateway implements CustomerTotalsGateway{
	private static final String ID_SEPARATOR = ",";
	private static final String TOTALS_QUERY = """
			SELECT
			    assumeNotNull(customer_id) AS linked_customer_id,
			    toInt64(count()) AS orders,
			    toDecimal64(sum(total_price), 2) AS spend,
			    toUnixTimestamp64Milli(max(created_at)) AS last_order_at
			FROM orders_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			  AND status != 'CANCELLED'
			  AND customer_id IS NOT NULL
			  AND has(splitByChar(',', {customerIds:String}), assumeNotNull(customer_id))
			GROUP BY linked_customer_id
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private final AnalyticStore store;

	@Override
	public List<CustomerTotalsProjection> totals(TenantId tenantId, Set<String> customerIds){
		Map<String, Object> params = Map.of(
				"tenantId", tenantId.value(),
				"customerIds", String.join(ID_SEPARATOR, customerIds));
		return store.query(TOTALS_QUERY, params,
				reader -> new CustomerTotalsProjection(reader.getString("linked_customer_id"),
						reader.getLong("orders"), reader.getBigDecimal("spend"),
						Instant.ofEpochMilli(reader.getLong("last_order_at"))));
	}

}
