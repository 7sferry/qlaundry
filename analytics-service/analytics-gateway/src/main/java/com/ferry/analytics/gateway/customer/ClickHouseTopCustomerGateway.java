package com.ferry.analytics.gateway.customer;

import com.ferry.analytics.core.customer.top.TopCustomerGateway;
import com.ferry.analytics.domain.customer.TopCustomerProjection;
import com.ferry.analytics.domain.period.MonthToDateWindow;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.gateway.common.AnalyticStore;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class ClickHouseTopCustomerGateway implements TopCustomerGateway{
	private static final String TOP_QUERY = """
			SELECT
			    assumeNotNull(customer_id) AS linked_customer_id,
			    toInt64(count()) AS orders,
			    toDecimal64(sum(total_price), 2) AS spend
			FROM orders_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			  AND status != 'CANCELLED'
			  AND customer_id IS NOT NULL
			  AND created_at >= {from:DateTime64(3, 'UTC')}
			  AND created_at < {to:DateTime64(3, 'UTC')}
			  AND assumeNotNull(customer_id) IN (
			      SELECT customer_id
			      FROM customers_current FINAL
			      WHERE tenant_id = {tenantId:String}
			        AND deleted = 0)
			GROUP BY linked_customer_id
			ORDER BY spend DESC, linked_customer_id
			LIMIT {limit:UInt32}
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private final AnalyticStore store;

	@Override
	public List<TopCustomerProjection> top(TenantId tenantId, MonthToDateWindow window, int limit){
		Map<String, Object> params = Map.of(
				"tenantId", tenantId.value(),
				"from", store.dateTime(window.from()),
				"to", store.dateTime(window.to()),
				"limit", String.valueOf(limit));
		return store.query(TOP_QUERY, params,
				reader -> new TopCustomerProjection(reader.getString("linked_customer_id"), reader.getLong("orders"),
						reader.getBigDecimal("spend")));
	}

}
