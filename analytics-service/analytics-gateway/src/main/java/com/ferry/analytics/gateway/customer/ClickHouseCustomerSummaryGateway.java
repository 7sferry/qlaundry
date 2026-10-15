package com.ferry.analytics.gateway.customer;

import com.ferry.analytics.core.customer.summary.CustomerSummaryGateway;
import com.ferry.analytics.domain.customer.CustomerSummaryProjection;
import com.ferry.analytics.domain.period.MonthToDateWindow;
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
public class ClickHouseCustomerSummaryGateway implements CustomerSummaryGateway{
	private static final String CUSTOMER_COUNT_QUERY = """
			SELECT
			    toInt64(count()) AS total_customers,
			    toInt64(countIf(created_at >= {from:DateTime64(3, 'UTC')} AND created_at < {to:DateTime64(3, 'UTC')})) AS new_customers
			FROM customers_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private static final String MONTH_SPEND_QUERY = """
			SELECT
			    toDecimal64(sum(total_price), 2) AS month_spend,
			    toInt64(uniqExact(customer_id)) AS spending_customers
			FROM orders_current FINAL
			WHERE tenant_id = {tenantId:String}
			  AND deleted = 0
			  AND status != 'CANCELLED'
			  AND customer_id IS NOT NULL
			  AND created_at >= {from:DateTime64(3, 'UTC')}
			  AND created_at < {to:DateTime64(3, 'UTC')}
			SETTINGS do_not_merge_across_partitions_select_final = 1
			""";

	private final AnalyticStore store;

	@Override
	public CustomerSummaryProjection summarize(TenantId tenantId, MonthToDateWindow window){
		Map<String, Object> params = Map.of(
				"tenantId", tenantId.value(),
				"from", store.dateTime(window.from()),
				"to", store.dateTime(window.to()));
		CustomerCounts counts = store.query(CUSTOMER_COUNT_QUERY, params,
						reader -> new CustomerCounts(reader.getLong("total_customers"), reader.getLong("new_customers")))
				.stream()
				.findFirst()
				.orElse(new CustomerCounts(0L, 0L));
		MonthSpend spend = store.query(MONTH_SPEND_QUERY, params,
						reader -> new MonthSpend(reader.getBigDecimal("month_spend"),
								reader.getLong("spending_customers")))
				.stream()
				.findFirst()
				.orElse(new MonthSpend(BigDecimal.ZERO, 0L));
		return new CustomerSummaryProjection(counts.total(), counts.fresh(), spend.amount(), spend.customers());
	}

	private record CustomerCounts(
		long total,
		long fresh){
	}

	private record MonthSpend(
		BigDecimal amount,
		long customers){
	}

}
